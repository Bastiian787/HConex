package com.hconex.core.proxy;

import com.hconex.config.HabboConfig;

/**
 * TCP Proxy Server
 */
public class ProxyServer {
    
    private final HabboConfig config;
    private boolean running = false;
    
    public ProxyServer(HabboConfig config) {
        this.config = config;
    }
    
    /**
     * Start the proxy server
     */
    public void start() throws InterruptedException {
        running = true;
        System.out.println("Proxy Server started on port " + config.getProxyPort());
        System.out.println("Connecting to " + config.getServerHost() + ":" + config.getServerPort());
    }
    
    /**
     * Wait for the server to stop
     */
    public void waitForShutdown() throws InterruptedException {
        while (running) {
            Thread.sleep(1000);
        }
    }
    
    /**
     * Stop the proxy server
     */
    public void stop() {
        System.out.println("Shutting down proxy server...");
        running = false;
    }
    
    /**
     * Check if running
     */
    public boolean isRunning() {
        return running;
    }
}
