package com.hconex;

import com.hconex.config.HabboConfig;
import com.hconex.core.proxy.ProxyServer;
import java.util.concurrent.CountDownLatch;

/**
 * Canonical application entry point for HConex.
 */
public final class Application {

    private Application() {
    }

    public static void main(String[] args) {
        if (!isHeadlessEnvironment()) {
            System.out.println("[INFO] Entorno con GUI detectado, pero esta build usa entrypoint de consola.");
        }
        runHeadlessProxy();
    }

    private static boolean isHeadlessEnvironment() {
        String display = System.getenv("DISPLAY");
        String waylandDisplay = System.getenv("WAYLAND_DISPLAY");
        return (display == null || display.isBlank())
                && (waylandDisplay == null || waylandDisplay.isBlank());
    }

    private static void runHeadlessProxy() {
        System.out.println("[INFO] Entorno sin GUI detectado. Iniciando HConex en modo consola...");
        System.out.println("[INFO] Proxy: localhost:" + HabboConfig.PROXY_PORT
                + " -> " + HabboConfig.SERVER_HOST + ":" + HabboConfig.SERVER_PORT);

        final ProxyServer proxyServer = new ProxyServer(
                HabboConfig.SERVER_HOST,
                HabboConfig.SERVER_PORT,
                HabboConfig.PROXY_PORT
        );

        final CountDownLatch stopLatch = new CountDownLatch(1);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                proxyServer.stop();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                stopLatch.countDown();
            }
        }));

        try {
            proxyServer.start();
            System.out.println("[SUCCESS] Proxy corriendo. Presiona Ctrl+C para detener.");
            stopLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("[ERROR] Ejecución interrumpida: " + e.getMessage());
        }
    }
}
