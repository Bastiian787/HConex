package com.hconex.proxy;

import com.hconex.config.HabboConfig;
import com.hconex.core.proxy.ProxyServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ProxyServer}.
 */
@DisplayName("ProxyServer")
class ProxyServerTest {

    private ProxyServer proxyServer;

    @AfterEach
    void tearDown() throws Exception {
        if (proxyServer != null && proxyServer.isRunning()) {
            proxyServer.stop();
        }
    }

    @Test
    @DisplayName("Default constructor uses HabboConfig values")
    void defaultConstructor_usesHabboConfig() {
        proxyServer = new ProxyServer();
        assertEquals(HabboConfig.SERVER_HOST, proxyServer.getRemoteHost());
        assertEquals(HabboConfig.SERVER_PORT, proxyServer.getRemotePort());
        assertEquals(HabboConfig.PROXY_PORT, proxyServer.getLocalPort());
    }

    @Test
    @DisplayName("Custom constructor stores provided values")
    void customConstructor_storesValues() {
        proxyServer = new ProxyServer("example.com", 12345, 9090);
        assertEquals("example.com", proxyServer.getRemoteHost());
        assertEquals(12345, proxyServer.getRemotePort());
        assertEquals(9090, proxyServer.getLocalPort());
    }

    @Test
    @DisplayName("isRunning returns false before start")
    void isRunning_falseBeforeStart() {
        proxyServer = new ProxyServer();
        assertFalse(proxyServer.isRunning());
    }

    @Test
    @DisplayName("start makes isRunning return true")
    void start_setsRunningTrue() throws Exception {
        // Use a high port unlikely to be in use
        proxyServer = new ProxyServer("habbo.es", 30000, 19876);
        proxyServer.start();
        assertTrue(proxyServer.isRunning());
    }

    @Test
    @DisplayName("stop after start makes isRunning return false")
    void stop_afterStart_setsRunningFalse() throws Exception {
        proxyServer = new ProxyServer("habbo.es", 30000, 19877);
        proxyServer.start();
        proxyServer.stop();
        assertFalse(proxyServer.isRunning());
    }

    @Test
    @DisplayName("start when already running does not throw")
    void start_alreadyRunning_noException() throws Exception {
        proxyServer = new ProxyServer("habbo.es", 30000, 19878);
        proxyServer.start();
        assertDoesNotThrow(() -> proxyServer.start());
    }
}
