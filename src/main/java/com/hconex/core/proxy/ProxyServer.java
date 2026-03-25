package com.hconex.core.proxy;

import com.hconex.config.HabboConfig;

/**
 * TCP Proxy Server
 */
public class ProxyServer {

    private final String remoteHost;
    private final int remotePort;
    private final int localPort;
    private boolean running = false;

    public ProxyServer(HabboConfig config) {
        this.remoteHost = config.getServerHost();
        this.remotePort = config.getServerPort();
        this.localPort = config.getProxyPort();
    }

    public ProxyServer(String remoteHost, int remotePort, int localPort) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.localPort = localPort;
    }
    
    /**
     * Start the proxy server
     */
    public void start() throws InterruptedException {
        running = true;
        System.out.println("Proxy Server started on port " + localPort);
        System.out.println("Connecting to " + remoteHost + ":" + remotePort);
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
    public void stop() throws InterruptedException {
        System.out.println("Shutting down proxy server...");
        running = false;
    }
    
    /**
     * Check if running
     */
    public boolean isRunning() {
        return running;
    }

    public int getLocalPort() {
        return localPort;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public int getRemotePort() {
        return remotePort;
    }
}
