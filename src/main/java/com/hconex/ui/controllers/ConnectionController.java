package com.hconex.ui.controllers;

import com.hconex.config.HabboConfig;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the connection settings panel ({@code settings.fxml}).
 * <p>
 * Displays and allows editing of the proxy and remote server configuration.
 * Changes take effect on the next proxy start.
 * </p>
 */
public class ConnectionController implements Initializable {

    private static final Logger logger = LogManager.getLogger(ConnectionController.class);

    // -------------------------------------------------------------------------
    // FXML-injected controls
    // -------------------------------------------------------------------------

    @FXML private TextField txtRemoteHost;
    @FXML private TextField txtRemotePort;
    @FXML private TextField txtLocalPort;
    @FXML private Label lblConnectionInfo;

    // -------------------------------------------------------------------------
    // Initializable
    // -------------------------------------------------------------------------

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadDefaults();
        logger.info("ConnectionController initialised");
    }

    // -------------------------------------------------------------------------
    // FXML action handlers
    // -------------------------------------------------------------------------

    /**
     * Resets all fields to the default values from {@link HabboConfig}.
     */
    @FXML
    private void onResetDefaults() {
        loadDefaults();
        logger.info("Connection settings reset to defaults");
    }

    /**
     * Validates and saves the current field values.
     */
    @FXML
    private void onSave() {
        String host = txtRemoteHost != null ? txtRemoteHost.getText().trim() : "";
        String remotePortStr = txtRemotePort != null ? txtRemotePort.getText().trim() : "";
        String localPortStr = txtLocalPort != null ? txtLocalPort.getText().trim() : "";

        if (host.isEmpty()) {
            setInfo("El servidor remoto no puede estar vacío.");
            return;
        }

        try {
            int remotePort = Integer.parseInt(remotePortStr);
            int localPort = Integer.parseInt(localPortStr);

            if (remotePort < 1 || remotePort > 65535 || localPort < 1 || localPort > 65535) {
                setInfo("Los puertos deben estar entre 1 y 65535.");
                return;
            }

            setInfo(String.format("Configuración guardada: %s:%d → localhost:%d",
                    host, remotePort, localPort));
            logger.info("Connection settings saved: {}:{} → localhost:{}",
                    host, remotePort, localPort);
        } catch (NumberFormatException e) {
            setInfo("Puerto inválido – introduce un número entero.");
        }
    }

    // -------------------------------------------------------------------------
    // Getters for current values
    // -------------------------------------------------------------------------

    /**
     * Returns the remote host currently entered in the UI.
     *
     * @return remote host string
     */
    public String getRemoteHost() {
        return txtRemoteHost != null ? txtRemoteHost.getText().trim() : HabboConfig.SERVER_HOST;
    }

    /**
     * Returns the remote port currently entered in the UI, or the default if invalid.
     *
     * @return remote port
     */
    public int getRemotePort() {
        try {
            return Integer.parseInt(txtRemotePort.getText().trim());
        } catch (NumberFormatException e) {
            return HabboConfig.SERVER_PORT;
        }
    }

    /**
     * Returns the local proxy port currently entered in the UI, or the default if invalid.
     *
     * @return local proxy port
     */
    public int getLocalPort() {
        try {
            return Integer.parseInt(txtLocalPort.getText().trim());
        } catch (NumberFormatException e) {
            return HabboConfig.PROXY_PORT;
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private void loadDefaults() {
        if (txtRemoteHost != null) {
            txtRemoteHost.setText(HabboConfig.SERVER_HOST);
        }
        if (txtRemotePort != null) {
            txtRemotePort.setText(String.valueOf(HabboConfig.SERVER_PORT));
        }
        if (txtLocalPort != null) {
            txtLocalPort.setText(String.valueOf(HabboConfig.PROXY_PORT));
        }
        setInfo("Valores por defecto cargados.");
    }

    private void setInfo(String message) {
        if (lblConnectionInfo != null) {
            lblConnectionInfo.setText(message);
        }
    }
}
