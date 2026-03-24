package com.hconex;

import com.hconex.config.HabboConfig;
import com.hconex.core.proxy.ProxyServer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * HConex - Habbo Hotel interceptor proxy application.
 * <p>
 * Entry point for the JavaFX desktop application. Initialises the main window
 * and starts the background proxy server.
 * </p>
 */
public class HConexApplication extends Application {

    private static final Logger logger = LogManager.getLogger(HConexApplication.class);

    private ProxyServer proxyServer;

    /**
     * JavaFX application start method.
     *
     * @param primaryStage the primary stage provided by the JavaFX runtime
     * @throws Exception if the FXML layout cannot be loaded
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("Starting HConex v{}", HabboConfig.APP_VERSION);

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/main.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(
                getClass().getResource("/css/style.css").toExternalForm());

        primaryStage.setTitle("HConex " + HabboConfig.APP_VERSION);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(450);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            logger.info("Application closing – stopping proxy server");
            stopProxy();
            Platform.exit();
        });

        logger.info("HConex UI initialised successfully");
    }

    /**
     * Stops the proxy server gracefully.
     */
    private void stopProxy() {
        if (proxyServer != null) {
            try {
                proxyServer.stop();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Interrupted while stopping proxy server", e);
            }
        }
    }

    /**
     * Application entry point.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
