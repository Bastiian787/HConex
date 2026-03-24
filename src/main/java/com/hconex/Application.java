package com.hconex;

import com.hconex.config.HabboConfig;
import com.hconex.core.protocol.HabboProtocol;
import com.hconex.core.protocol.PacketFactory;
import com.hconex.core.proxy.ProxyServer;
import com.hconex.logging.PacketLogger;
import com.hconex.core.packets.Packet;

/**
 * Main Application class for HConex v0.0.1
 */
public class Application {
    
    private static ProxyServer proxyServer;
    private static PacketLogger packetLogger;
    private static HabboConfig config;
    private static boolean running = false;
    
    public static void main(String[] args) {
        try {
            System.out.println("========================================");
            System.out.println("HConex v0.0.1 - Habbo Interceptor Proxy");
            System.out.println("========================================");
            
            // Initialize configuration
            config = new HabboConfig();
            System.out.println("Configuration loaded:");
            System.out.println("  Proxy Port: " + config.getProxyPort());
            System.out.println("  Habbo Host: " + config.getServerHost() + ":" + config.getServerPort());
            
            // Initialize packet logger
            packetLogger = new PacketLogger();
            System.out.println("Packet logger initialized");
            
            // Initialize proxy server
            proxyServer = new ProxyServer(config);
            System.out.println("Proxy server initialized on port " + config.getProxyPort());
            
            // Start proxy server
            System.out.println("Starting proxy server...");
            proxyServer.start();
            running = true;
            
            System.out.println("========================================");
            System.out.println("Proxy server is running!");
            System.out.println("Connect your client to localhost:" + config.getProxyPort());
            System.out.println("Press Ctrl+C to stop");
            System.out.println("========================================");
            
            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutdown signal received...");
                shutdown();
            }));
            
            // Wait for server to stop
            proxyServer.waitForShutdown();
            
        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
            e.printStackTrace();
            shutdown();
            System.exit(1);
        }
    }
    
    /**
     * Graceful shutdown
     */
    private static void shutdown() {
        System.out.println("Shutting down application...");
        running = false;
        
        if (proxyServer != null) {
            proxyServer.stop();
            System.out.println("Proxy server stopped");
        }
        
        if (packetLogger != null) {
            System.out.println("Total packets logged: " + packetLogger.getLogCount());
        }
        
        System.out.println("Application shutdown complete");
    }
    
    public static int getLoggedPackets() {
        return packetLogger != null ? packetLogger.getLogCount() : 0;
    }
    
    public static PacketLogger getPacketLogger() {
        return packetLogger;
    }
    
    public static boolean isRunning() {
        return running;
    }
}
