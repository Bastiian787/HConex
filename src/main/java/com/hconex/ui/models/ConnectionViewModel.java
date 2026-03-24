package com.hconex.ui.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * JavaFX ViewModel representing the current connection state.
 * <p>
 * Binds to UI controls so that connection status is reflected in the interface
 * automatically whenever properties change.
 * </p>
 */
public final class ConnectionViewModel {

    private final BooleanProperty connected;
    private final StringProperty statusText;
    private final StringProperty remoteHost;
    private final IntegerProperty remotePort;
    private final IntegerProperty localPort;
    private final IntegerProperty activeConnections;

    /**
     * Creates a disconnected ViewModel with default values.
     */
    public ConnectionViewModel() {
        this.connected = new SimpleBooleanProperty(false);
        this.statusText = new SimpleStringProperty("Desconectado");
        this.remoteHost = new SimpleStringProperty("");
        this.remotePort = new SimpleIntegerProperty(0);
        this.localPort = new SimpleIntegerProperty(0);
        this.activeConnections = new SimpleIntegerProperty(0);
    }

    // -------------------------------------------------------------------------
    // connected
    // -------------------------------------------------------------------------

    public boolean isConnected() { return connected.get(); }
    public void setConnected(boolean value) { connected.set(value); }
    public BooleanProperty connectedProperty() { return connected; }

    // -------------------------------------------------------------------------
    // statusText
    // -------------------------------------------------------------------------

    public String getStatusText() { return statusText.get(); }
    public void setStatusText(String value) { statusText.set(value); }
    public StringProperty statusTextProperty() { return statusText; }

    // -------------------------------------------------------------------------
    // remoteHost
    // -------------------------------------------------------------------------

    public String getRemoteHost() { return remoteHost.get(); }
    public void setRemoteHost(String value) { remoteHost.set(value); }
    public StringProperty remoteHostProperty() { return remoteHost; }

    // -------------------------------------------------------------------------
    // remotePort
    // -------------------------------------------------------------------------

    public int getRemotePort() { return remotePort.get(); }
    public void setRemotePort(int value) { remotePort.set(value); }
    public IntegerProperty remotePortProperty() { return remotePort; }

    // -------------------------------------------------------------------------
    // localPort
    // -------------------------------------------------------------------------

    public int getLocalPort() { return localPort.get(); }
    public void setLocalPort(int value) { localPort.set(value); }
    public IntegerProperty localPortProperty() { return localPort; }

    // -------------------------------------------------------------------------
    // activeConnections
    // -------------------------------------------------------------------------

    public int getActiveConnections() { return activeConnections.get(); }
    public void setActiveConnections(int value) { activeConnections.set(value); }
    public IntegerProperty activeConnectionsProperty() { return activeConnections; }
}
