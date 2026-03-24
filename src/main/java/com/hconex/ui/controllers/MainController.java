package com.hconex.ui.controllers;

import com.hconex.config.HabboConfig;
import com.hconex.core.packets.Packet;
import com.hconex.core.proxy.ConnectionManager;
import com.hconex.core.proxy.ProxyServer;
import com.hconex.logging.PacketLogger;
import com.hconex.ui.models.ConnectionViewModel;
import com.hconex.ui.models.PacketViewModel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Controller for the main application window ({@code main.fxml}).
 * <p>
 * Manages the proxy lifecycle (start / stop) and keeps the packet table
 * and status bar synchronised with the current application state.
 * </p>
 */
public class MainController implements Initializable {

    private static final Logger logger = LogManager.getLogger(MainController.class);

    // -------------------------------------------------------------------------
    // FXML-injected controls
    // -------------------------------------------------------------------------

    @FXML private Button btnStartProxy;
    @FXML private Button btnStopProxy;
    @FXML private Button btnClearLog;
    @FXML private Label lblStatus;
    @FXML private Label lblConnections;
    @FXML private Label lblPacketCount;

    @FXML private TableView<PacketViewModel> packetTable;
    @FXML private TableColumn<PacketViewModel, String> colTime;
    @FXML private TableColumn<PacketViewModel, String> colDirection;
    @FXML private TableColumn<PacketViewModel, String> colHeader;
    @FXML private TableColumn<PacketViewModel, Integer> colLength;
    @FXML private TableColumn<PacketViewModel, String> colHex;

    // -------------------------------------------------------------------------
    // Internal state
    // -------------------------------------------------------------------------

    private ProxyServer proxyServer;
    private final PacketLogger packetLogger = PacketLogger.getInstance();
    private final ConnectionViewModel connectionViewModel = new ConnectionViewModel();
    private final ObservableList<PacketViewModel> packetList = FXCollections.observableArrayList();
    private ScheduledExecutorService statusUpdater;

    // -------------------------------------------------------------------------
    // Initializable
    // -------------------------------------------------------------------------

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        setupBindings();
        startStatusUpdater();
        logger.info("MainController initialised");
    }

    // -------------------------------------------------------------------------
    // FXML action handlers
    // -------------------------------------------------------------------------

    /**
     * Starts the proxy server.  Called when the user clicks "Iniciar Proxy".
     */
    @FXML
    private void onStartProxy() {
        if (proxyServer != null && proxyServer.isRunning()) {
            logger.warn("Proxy is already running");
            return;
        }

        proxyServer = new ProxyServer(
                HabboConfig.SERVER_HOST,
                HabboConfig.SERVER_PORT,
                HabboConfig.PROXY_PORT);

        new Thread(() -> {
            try {
                proxyServer.start();
                Platform.runLater(() -> {
                    connectionViewModel.setConnected(true);
                    connectionViewModel.setStatusText(
                            "Proxy activo – escuchando en " + HabboConfig.PROXY_PORT);
                    connectionViewModel.setRemoteHost(HabboConfig.SERVER_HOST);
                    connectionViewModel.setRemotePort(HabboConfig.SERVER_PORT);
                    connectionViewModel.setLocalPort(HabboConfig.PROXY_PORT);
                    btnStartProxy.setDisable(true);
                    btnStopProxy.setDisable(false);
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Proxy start interrupted", e);
            } catch (Exception e) {
                logger.error("Failed to start proxy server", e);
                Platform.runLater(() ->
                        connectionViewModel.setStatusText("Error al iniciar: " + e.getMessage()));
            }
        }, "proxy-start-thread").start();
    }

    /**
     * Stops the proxy server.  Called when the user clicks "Detener Proxy".
     */
    @FXML
    private void onStopProxy() {
        if (proxyServer == null || !proxyServer.isRunning()) {
            return;
        }

        new Thread(() -> {
            try {
                proxyServer.stop();
                Platform.runLater(() -> {
                    connectionViewModel.setConnected(false);
                    connectionViewModel.setStatusText("Proxy detenido");
                    btnStartProxy.setDisable(false);
                    btnStopProxy.setDisable(true);
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Proxy stop interrupted", e);
            }
        }, "proxy-stop-thread").start();
    }

    /**
     * Clears the packet log.  Called when the user clicks "Limpiar Log".
     */
    @FXML
    private void onClearLog() {
        packetLogger.clear();
        packetList.clear();
        logger.info("Packet log cleared by user");
    }

    // -------------------------------------------------------------------------
    // Public API for other controllers / tests
    // -------------------------------------------------------------------------

    /**
     * Appends a captured packet to the table (must be called on the FX thread).
     *
     * @param packet the packet to display
     */
    public void addPacket(Packet packet) {
        Platform.runLater(() -> {
            packetList.add(new PacketViewModel(packet));
            lblPacketCount.setText("Paquetes: " + packetList.size());
        });
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private void setupTable() {
        colTime.setCellValueFactory(new PropertyValueFactory<>("timestampFormatted"));
        colDirection.setCellValueFactory(new PropertyValueFactory<>("direction"));
        colHeader.setCellValueFactory(new PropertyValueFactory<>("headerHex"));
        colLength.setCellValueFactory(new PropertyValueFactory<>("payloadLength"));
        colHex.setCellValueFactory(new PropertyValueFactory<>("hexDump"));
        packetTable.setItems(packetList);
    }

    private void setupBindings() {
        lblStatus.textProperty().bind(connectionViewModel.statusTextProperty());
        btnStopProxy.setDisable(true);
    }

    private void startStatusUpdater() {
        statusUpdater = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "status-updater");
            t.setDaemon(true);
            return t;
        });
        statusUpdater.scheduleAtFixedRate(() ->
                Platform.runLater(() ->
                        lblConnections.setText(
                                "Conexiones: " + ConnectionManager.getInstance().getActiveCount())),
                0, 1, TimeUnit.SECONDS);
    }

    /**
     * Shuts down the status updater; called on application exit.
     */
    public void shutdown() {
        if (statusUpdater != null && !statusUpdater.isShutdown()) {
            statusUpdater.shutdownNow();
        }
        if (proxyServer != null && proxyServer.isRunning()) {
            try {
                proxyServer.stop();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
