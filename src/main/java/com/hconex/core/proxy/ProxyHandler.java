package com.hconex.core.proxy;

import com.hconex.config.HabboConfig;
import com.hconex.core.packets.Packet;

/**
 * Handles bidirectional proxy between client and Habbo server
 */
public class ProxyHandler {
    
    private final HabboConfig config;
    private boolean connected = false;
    
    public ProxyHandler(HabboConfig config) {
        this.config = config;
    }
    
    /**
     * Handle client connection
     */
    public void onClientConnect(String clientAddress) {
        System.out.println("Client connected: " + clientAddress);
        connected = true;
    }
    
    /**
     * Handle incoming data from client
     */
    public byte[] handleClientData(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        
        // Log packet from client to server (OUTGOING)
        Packet packet = new Packet(0, data, Packet.Direction.OUTGOING);
        System.out.println("Client -> Server: " + packet);
        
        return data;
    }
    
    /**
     * Handle incoming data from server
     */
    public byte[] handleServerData(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        
        // Log packet from server to client (INCOMING)
        Packet packet = new Packet(0, data, Packet.Direction.INCOMING);
        System.out.println("Server -> Client: " + packet);
        
        return data;
    }
    
    /**
     * Handle client disconnection
     */
    public void onClientDisconnect(String clientAddress) {
        System.out.println("Client disconnected: " + clientAddress);
        connected = false;
    }
    
    /**
     * Check if connected
     */
    public boolean isConnected() {
        return connected;
    }
    
    /**
     * Get config
     */
    public HabboConfig getConfig() {
        return config;
    }
}
