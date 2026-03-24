package com.hconex.core.proxy;

import io.netty.channel.Channel;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages active client connections
 */
public class ConnectionManager {
    
    private static final Set<Channel> connections = ConcurrentHashMap.newKeySet();
    
    /**
     * Register a new connection
     */
    public static void addConnection(Channel channel) {
        connections.add(channel);
        System.out.println("Connection added. Total connections: " + connections.size());
    }
    
    /**
     * Remove a connection
     */
    public static void removeConnection(Channel channel) {
        connections.remove(channel);
        System.out.println("Connection removed. Total connections: " + connections.size());
    }
    
    /**
     * Get number of active connections
     */
    public static int getActiveConnections() {
        return connections.size();
    }
    
    /**
     * Close all connections
     */
    public static void closeAllConnections() {
        for (Channel channel : connections) {
            if (channel.isActive()) {
                channel.close();
            }
        }
        connections.clear();
    }
    
    /**
     * Get all active connections
     */
    public static Set<Channel> getConnections() {
        return Set.copyOf(connections);
    }
}
