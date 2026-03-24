package com.hconex.core.proxy;

import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe registry of active proxy connections.
 * <p>
 * Each entry maps an inbound (client-side) {@link Channel} to its corresponding
 * outbound (server-side) {@link Channel}.  The manager is a singleton so that
 * the UI and other components can query active connection counts at any time.
 * </p>
 */
public final class ConnectionManager {

    private static final Logger logger = LogManager.getLogger(ConnectionManager.class);

    /** Singleton holder (lazy, thread-safe via class-loading). */
    private static final class Holder {
        static final ConnectionManager INSTANCE = new ConnectionManager();
    }

    /** Maps inbound channel → outbound channel. */
    private final Map<Channel, Channel> connectionMap = new ConcurrentHashMap<>();

    /** Private constructor for singleton. */
    private ConnectionManager() {}

    /**
     * Returns the singleton instance.
     *
     * @return the {@link ConnectionManager} singleton
     */
    public static ConnectionManager getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Registers a new proxied connection pair.
     *
     * @param inbound  channel connected to the local Habbo client
     * @param outbound channel connected to the remote habbo.es server
     */
    public void register(Channel inbound, Channel outbound) {
        connectionMap.put(inbound, outbound);
        logger.debug("Connection registered: {} ↔ {} (total: {})",
                inbound.remoteAddress(), outbound.remoteAddress(), connectionMap.size());
    }

    /**
     * Removes the connection entry for the given inbound channel.
     *
     * @param inbound the client-side channel to remove
     */
    public void unregister(Channel inbound) {
        Channel outbound = connectionMap.remove(inbound);
        if (outbound != null) {
            logger.debug("Connection unregistered: {} (total: {})",
                    inbound.remoteAddress(), connectionMap.size());
        }
    }

    /**
     * Returns the number of currently active connections.
     *
     * @return active connection count
     */
    public int getActiveCount() {
        return connectionMap.size();
    }

    /**
     * Returns an unmodifiable view of all registered inbound channels.
     *
     * @return set of inbound channels
     */
    public Set<Channel> getInboundChannels() {
        return Collections.unmodifiableSet(connectionMap.keySet());
    }

    /**
     * Returns the outbound channel associated with the given inbound channel,
     * or {@code null} if no such mapping exists.
     *
     * @param inbound inbound channel to look up
     * @return associated outbound channel, or {@code null}
     */
    public Channel getOutbound(Channel inbound) {
        return connectionMap.get(inbound);
    }

    /**
     * Closes all active connections and clears the registry.
     */
    public void closeAll() {
        connectionMap.forEach((in, out) -> {
            ProxyHandler.closeOnFlush(in);
            ProxyHandler.closeOnFlush(out);
        });
        connectionMap.clear();
        logger.info("All connections closed");
    }
}
