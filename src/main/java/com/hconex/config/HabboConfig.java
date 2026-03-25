package com.hconex.config;

public class HabboConfig {
    public static final String HABBO_HOST = "habbo.es";
    public static final int HABBO_PORT = 30000;
    public static final int PROXY_PORT = 8080;

    public static final String SERVER_HOST = HABBO_HOST;
    public static final int SERVER_PORT = HABBO_PORT;
    
    private String serverHost = HABBO_HOST;
    private int serverPort = HABBO_PORT;
    private int proxyPort = PROXY_PORT;
    
    public String getServerHost() { return serverHost; }
    public int getServerPort() { return serverPort; }
    public int getProxyPort() { return proxyPort; }
}
