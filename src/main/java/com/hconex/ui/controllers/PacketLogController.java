package com.hconex.ui.controllers;

import com.hconex.core.packets.Packet;
import com.hconex.logging.LogEntry;
import com.hconex.logging.PacketLogger;
import com.hconex.ui.models.PacketViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controller for the packet log panel ({@code packet-log.fxml}).
 * <p>
 * Displays intercepted packets in a filterable table.  Supports filtering by
 * direction and header ID.
 * </p>
 */
public class PacketLogController implements Initializable {

    private static final Logger logger = LogManager.getLogger(PacketLogController.class);

    // -------------------------------------------------------------------------
    // FXML-injected controls
    // -------------------------------------------------------------------------

    @FXML private TableView<PacketViewModel> logTable;
    @FXML private TableColumn<PacketViewModel, String> colTime;
    @FXML private TableColumn<PacketViewModel, String> colDirection;
    @FXML private TableColumn<PacketViewModel, String> colHeader;
    @FXML private TableColumn<PacketViewModel, Integer> colLength;
    @FXML private TableColumn<PacketViewModel, String> colHex;

    @FXML private CheckBox chkShowIncoming;
    @FXML private CheckBox chkShowOutgoing;
    @FXML private TextField txtHeaderFilter;
    @FXML private Button btnRefresh;
    @FXML private Button btnClear;

    // -------------------------------------------------------------------------
    // Internal state
    // -------------------------------------------------------------------------

    private final PacketLogger packetLogger = PacketLogger.getInstance();
    private final ObservableList<PacketViewModel> displayedEntries =
            FXCollections.observableArrayList();

    // -------------------------------------------------------------------------
    // Initializable
    // -------------------------------------------------------------------------

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        setupFilters();
        refreshLog();
        logger.info("PacketLogController initialised");
    }

    // -------------------------------------------------------------------------
    // FXML action handlers
    // -------------------------------------------------------------------------

    /** Refreshes the table from the in-memory log applying current filters. */
    @FXML
    private void onRefresh() {
        refreshLog();
    }

    /** Clears both the in-memory log and the table. */
    @FXML
    private void onClear() {
        packetLogger.clear();
        displayedEntries.clear();
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Appends a single packet to the log table (does not need to be on the FX thread –
     * call {@code Platform.runLater} if calling from a non-FX thread).
     *
     * @param packet the packet to display
     */
    public void append(Packet packet) {
        if (shouldShow(packet.getDirection())) {
            displayedEntries.add(new PacketViewModel(packet));
        }
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
        logTable.setItems(displayedEntries);
    }

    private void setupFilters() {
        if (chkShowIncoming != null) {
            chkShowIncoming.selectedProperty().addListener((obs, old, val) -> refreshLog());
        }
        if (chkShowOutgoing != null) {
            chkShowOutgoing.selectedProperty().addListener((obs, old, val) -> refreshLog());
        }
        if (txtHeaderFilter != null) {
            txtHeaderFilter.textProperty().addListener((obs, old, val) -> refreshLog());
        }
    }

    private void refreshLog() {
        displayedEntries.clear();
        List<LogEntry> entries = packetLogger.getEntries();
        String headerFilter = txtHeaderFilter != null ? txtHeaderFilter.getText().trim() : "";

        List<PacketViewModel> filtered = entries.stream()
                .filter(e -> shouldShow(e.getDirection()))
                .filter(e -> matchesHeaderFilter(e.getHeaderId(), headerFilter))
                .map(e -> new PacketViewModel(
                        new Packet(e.getHeaderId(), e.getRawData(), e.getDirection())))
                .collect(Collectors.toList());

        displayedEntries.addAll(filtered);
    }

    private boolean shouldShow(Packet.Direction direction) {
        if (direction == Packet.Direction.INCOMING) {
            return chkShowIncoming == null || chkShowIncoming.isSelected();
        } else {
            return chkShowOutgoing == null || chkShowOutgoing.isSelected();
        }
    }

    private boolean matchesHeaderFilter(int headerId, String filter) {
        if (filter.isEmpty()) {
            return true;
        }
        try {
            int filterValue = filter.startsWith("0x") || filter.startsWith("0X")
                    ? Integer.parseInt(filter.substring(2), 16)
                    : Integer.parseInt(filter);
            return headerId == filterValue;
        } catch (NumberFormatException e) {
            return true;
        }
    }
}
