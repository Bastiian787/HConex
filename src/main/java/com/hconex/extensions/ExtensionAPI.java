package com.hconex.extensions;

import com.hconex.core.packets.Packet;
import com.hconex.core.proxy.ProxyServer;
import com.hconex.logging.PacketLogger;

/**
 * API surface exposed to {@link Extension} implementations.
 * <p>
 * Extensions interact with HConex exclusively through this interface to
 * keep them decoupled from implementation details.
 * </p>
 */
public class ExtensionAPI {

    private final ProxyServer proxyServer;
    private final PacketLogger packetLogger;

    /**
     * Creates a new API instance backed by the given proxy and logger.
     *
     * @param proxyServer  the running proxy server
     * @param packetLogger the packet logger
     */
    public ExtensionAPI(ProxyServer proxyServer, PacketLogger packetLogger) {
        this.proxyServer = proxyServer;
        this.packetLogger = packetLogger;
    }

    /**
     * Returns {@code true} if the proxy server is currently running.
     *
     * @return proxy running state
     */
    public boolean isProxyRunning() {
        return proxyServer.isRunning();
    }

    /**
     * Returns the local port the proxy is listening on.
     *
     * @return local proxy port
     */
    public int getProxyPort() {
        return proxyServer.getLocalPort();
    }

    /**
     * Returns the hostname of the remote habbo.es server.
     *
     * @return remote host
     */
    public String getRemoteHost() {
        return proxyServer.getRemoteHost();
    }

    /**
     * Returns the port of the remote habbo.es server.
     *
     * @return remote port
     */
    public int getRemotePort() {
        return proxyServer.getRemotePort();
    }

    /**
     * Returns the number of packets currently stored in the log.
     *
     * @return log entry count
     */
    public int getLogSize() {
        return packetLogger.size();
    }

    /**
     * Clears the packet log.
     */
    public void clearLog() {
        packetLogger.clear();
    }

    /**
     * Logs a packet manually from an extension.
     *
     * @param packet the packet to log
     */
    public void logPacket(Packet packet) {
        packetLogger.log(packet);
    }
}
