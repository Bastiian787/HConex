package com.hconex;

import com.hconex.config.HabboConfig;
import com.hconex.core.proxy.ProxyServer;
import java.awt.GraphicsEnvironment;
import java.util.concurrent.CountDownLatch;

/**
 * Canonical application entry point for HConex.
 */
public final class Application {

    private Application() {
    }

    public static void main(String[] args) {
        if (isHeadlessRequested(args) || isHeadlessEnvironment()) {
            runHeadlessProxy();
            return;
        }

        System.out.println("[INFO] Entorno con GUI detectado. Iniciando interfaz de HConex...");
        try {
            HConexApplication.launchApp(args);
        } catch (Throwable error) {
            System.err.println("[WARN] No se pudo iniciar la interfaz gráfica: " + error.getMessage());
            System.err.println("[WARN] Entrando en modo consola...");
            runHeadlessProxy();
        }
    }

    private static boolean isHeadlessRequested(String[] args) {
        if (args == null) {
            return false;
        }
        for (String arg : args) {
            if ("--headless".equalsIgnoreCase(arg)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isHeadlessEnvironment() {
        return GraphicsEnvironment.isHeadless();
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
