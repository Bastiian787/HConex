package com.hconex.config;

/**
 * Central configuration for the HConex proxy application.
 * <p>
 * All connection parameters for the Habbo.es server and the local proxy are
 * defined here so that they can be changed in a single place without hunting
 * for hard-coded values throughout the codebase.
 * </p>
 */
public final class HabboConfig {

    // -------------------------------------------------------------------------
    // Application metadata
    // -------------------------------------------------------------------------

    /** Current application version. */
    public static final String APP_VERSION = "0.0.1";

    /** Application display name. */
    public static final String APP_NAME = "HConex";

    // -------------------------------------------------------------------------
    // Remote server (habbo.es)
    // -------------------------------------------------------------------------

    /** Hostname of the official Spanish Habbo Hotel server. */
    public static final String SERVER_HOST = "habbo.es";

    /** Primary game-server port used by habbo.es. */
    public static final int SERVER_PORT = 30000;

    /** Connection timeout to the remote server in milliseconds. */
    public static final int SERVER_CONNECT_TIMEOUT_MS = 10_000;

    // -------------------------------------------------------------------------
    // Local proxy
    // -------------------------------------------------------------------------

    /** Local address the proxy server binds to. */
    public static final String PROXY_HOST = "localhost";

    /** Local port the proxy server listens on. */
    public static final int PROXY_PORT = 8080;

    /** Maximum number of concurrent client connections accepted by the proxy. */
    public static final int MAX_CONNECTIONS = 10;

    // -------------------------------------------------------------------------
    // Packet logger
    // -------------------------------------------------------------------------

    /** Maximum number of packets kept in the in-memory log history. */
    public static final int LOG_HISTORY_SIZE = 2_000;

    // -------------------------------------------------------------------------
    // Private constructor – utility class must not be instantiated
    // -------------------------------------------------------------------------

    private HabboConfig() {
        throw new UnsupportedOperationException("HabboConfig is a utility class");
    }
}
