package com.hconex;

import com.hconex.config.HabboConfig;
import com.hconex.core.protocol.HabboProtocol;
import com.hconex.core.protocol.PacketFactory;
import com.hconex.core.proxy.ProxyServer;
import com.hconex.logging.PacketLogger;
import com.hconex.core.packets.Packet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main Application class that integrates ProxyServer, PacketLogger, and Protocol Parser
 */
public class Application {
    
    private static final Logger logger = LogManager.getLogger(Application.class);
    
    private static ProxyServer proxyServer;
    private static PacketLogger packetLogger;
    private static HabboConfig config;
    private static boolean running = false;
    
    public static void main(String[] args) {
        try {
            logger.info("========================================");
            logger.info("HConex v0.0.1 - Habbo Interceptor Proxy");
            logger.info("========================================");
            
            // Initialize configuration
            config = new HabboConfig();
            logger.info("Configuration loaded:");
            logger.info("  Proxy Port: {}", config.getProxyPort());
            logger.info("  Habbo Host: {}:{}", config.getServerHost(), config.getServerPort());
            
            // Initialize packet logger
            packetLogger = new PacketLogger();
            logger.info("Packet logger initialized");
            
            // Initialize proxy server
            proxyServer = new ProxyServer(config);
            logger.info("Proxy server initialized on port {}", config.getProxyPort());
            
            // Start proxy server
            logger.info("Starting proxy server...");
            proxyServer.start();
            running = true;
            
            logger.info("========================================");
            logger.info("Proxy server is running!");
            logger.info("Connect your client to localhost:{}", config.getProxyPort());
            logger.info("Press Ctrl+C to stop");
            logger.info("========================================");
            
            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Shutdown signal received...");
                shutdown();
            }));
            
            // Wait for server to stop
            proxyServer.waitForShutdown();
            
        } catch (Exception e) {
            logger.error("Fatal error: {}", e.getMessage());
            e.printStackTrace();
            shutdown();
            System.exit(1);
        }
    }
    
    /**
     * Log intercepted packet
     */
    public static void logPacket(byte[] data, Packet.Direction direction) {
        if (data == null || data.length == 0) {
            return;
        }
        
        try {
            // Parse packet info
            if (HabboProtocol.isPacketComplete(data)) {
                int packetId = HabboProtocol.getPacketId(data);
                byte[] payload = HabboProtocol.getPacketPayload(data);
                
                Packet packet = new Packet(packetId, payload, direction);
                packetLogger.log(packet);
                
                String packetInfo = PacketFactory.createPacketInfo(packetId, payload);
                logger.debug("{}: {}", direction, packetInfo);
            }
        } catch (Exception e) {
            logger.debug("Error parsing packet: {}", e.getMessage());
        }
    }
    
    /**
     * Get total logged packets
     */
    public static int getLoggedPackets() {
        return packetLogger != null ? packetLogger.getLogCount() : 0;
    }
    
    /**
     * Get packet logger
     */
    public static PacketLogger getPacketLogger() {
        return packetLogger;
    }
    
    /**
     * Get proxy server
     */
    public static ProxyServer getProxyServer() {
        return proxyServer;
    }
    
    /**
     * Get configuration
     */
    public static HabboConfig getConfig() {
        return config;
    }
    
    /**
     * Check if application is running
     */
    public static boolean isRunning() {
        return running;
    }
    
    /**
     * Graceful shutdown
     */
    private static void shutdown() {
        logger.info("Shutting down application...");
        running = false;
        
        if (proxyServer != null) {
            proxyServer.stop();
            logger.info("Proxy server stopped");
        }
        
        if (packetLogger != null) {
            logger.info("Total packets logged: {}", packetLogger.getLogCount());
        }
        
        logger.info("Application shutdown complete");
    }
}
