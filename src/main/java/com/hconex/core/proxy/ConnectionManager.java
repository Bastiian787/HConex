package com.hconex.core.proxy;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages active client connections
 */
public class ConnectionManager {
    
    private static final Set<String> connections = ConcurrentHashMap.newKeySet();
    
    /**
     * Add a connection
     */
    public static void addConnection(String clientId) {
        connections.add(clientId);
        System.out.println("Connection added. Total: " + connections.size());
    }
    
    /**
     * Remove a connection
     */
    public static void removeConnection(String clientId) {
        connections.remove(clientId);
        System.out.println("Connection removed. Total: " + connections.size());
    }
    
    /**
     * Get active connections count
     */
    public static int getActiveConnections() {
        return connections.size();
    }
}
