package com.hconex;

import com.hconex.config.HabboConfig;
import com.hconex.core.packets.Packet;
import com.hconex.core.proxy.ProxyServer;
import com.hconex.logging.PacketLogger;
import com.hconex.ui.ConsoleUI;

/**
 * Main Application class for HConex v0.0.1
 */
public class Application {
    
    private static ProxyServer proxyServer;
    private static PacketLogger packetLogger;
    private static HabboConfig config;
    private static final ConsoleUI consoleUI = new ConsoleUI();
    private static boolean running = false;
    private static int incomingCount = 0;
    private static int outgoingCount = 0;
    
    public static void main(String[] args) {
        try {
            consoleUI.printHeader("HConex v0.0.1 - Habbo Interceptor Proxy");
            
            // Initialize configuration
            config = new HabboConfig();
            consoleUI.printConfig("Proxy Port", String.valueOf(config.getProxyPort()));
            consoleUI.printConfig("Habbo Host", config.getServerHost() + ":" + config.getServerPort());
            
            // Initialize packet logger
            packetLogger = new PacketLogger();
            consoleUI.printSuccess("Packet logger initialized");
            
            // Initialize proxy server
            proxyServer = new ProxyServer(config);
            consoleUI.printSuccess("Proxy server initialized on port " + config.getProxyPort());
            
            // Start proxy server
            consoleUI.printRunning("Starting proxy server...");
            proxyServer.start();
            running = true;
            
            consoleUI.printRunning("Proxy server is running");
            consoleUI.printInfo("Connect your client to localhost:" + config.getProxyPort());
            consoleUI.printInfo("Press Ctrl+C to stop");
            consoleUI.printPacketTableHeader();
            
            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                consoleUI.printInfo("Shutdown signal received...");
                consoleUI.printStats(incomingCount, outgoingCount, incomingCount + outgoingCount);
                shutdown();
            }));
            
            // Wait for server to stop
            proxyServer.waitForShutdown();
            
        } catch (Exception e) {
            consoleUI.printError("Fatal error: " + e.getMessage());
            e.printStackTrace();
            shutdown();
            System.exit(1);
        }
    }
    
    /**
     * Graceful shutdown
     */
    private static void shutdown() {
        consoleUI.printInfo("Shutting down application...");
        running = false;
        
        if (proxyServer != null) {
            proxyServer.stop();
            consoleUI.printSuccess("Proxy server stopped");
        }
        
        if (packetLogger != null) {
            consoleUI.printInfo("Total packets logged: " + packetLogger.getLogCount());
        }
        
        consoleUI.printSuccess("Application shutdown complete");
    }

    public static void logPacket(Packet packet) {
        if (packet == null) {
            return;
        }

        if (packetLogger != null) {
            packetLogger.log(packet);
        }

        if (packet.getDirection() == Packet.Direction.INCOMING) {
            incomingCount++;
        } else if (packet.getDirection() == Packet.Direction.OUTGOING) {
            outgoingCount++;
        }

        consoleUI.printPacket(packet);
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
