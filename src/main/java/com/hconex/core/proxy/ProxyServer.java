package com.hconex.core.proxy;

import com.hconex.config.HabboConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Netty-based TCP proxy server for HConex.
 * <p>
 * Binds on the configured local port and, for every incoming connection,
 * creates a {@link ProxyHandler} that tunnels traffic to {@code habbo.es}.
 * </p>
 */
public class ProxyServer {

    private static final Logger logger = LogManager.getLogger(ProxyServer.class);

    private final String remoteHost;
    private final int remotePort;
    private final int localPort;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;

    private volatile boolean running = false;

    /**
     * Creates a ProxyServer using the default values from {@link HabboConfig}.
     */
    public ProxyServer() {
        this(HabboConfig.SERVER_HOST, HabboConfig.SERVER_PORT, HabboConfig.PROXY_PORT);
    }

    /**
     * Creates a ProxyServer with custom connection parameters.
     *
     * @param remoteHost hostname of the remote server to proxy to
     * @param remotePort port of the remote server
     * @param localPort  local port to bind the proxy listener on
     */
    public ProxyServer(String remoteHost, int remotePort, int localPort) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.localPort = localPort;
    }

    /**
     * Starts the proxy server asynchronously.
     * <p>
     * Binds the server to {@code localPort} and begins accepting connections.
     * Returns immediately; use {@link #isRunning()} to check status.
     * </p>
     *
     * @throws InterruptedException if the bind operation is interrupted
     */
    public void start() throws InterruptedException {
        if (running) {
            logger.warn("ProxyServer is already running on port {}", localPort);
            return;
        }

        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(
                                new ProxyHandler(remoteHost, remotePort));
                    }
                })
                .childOption(ChannelOption.AUTO_READ, false)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        ChannelFuture bindFuture = bootstrap.bind(localPort).sync();
        serverChannel = bindFuture.channel();
        running = true;

        logger.info("ProxyServer started – listening on port {} → {}:{}",
                localPort, remoteHost, remotePort);
    }

    /**
     * Stops the proxy server and releases all Netty resources.
     *
     * @throws InterruptedException if the shutdown is interrupted
     */
    public void stop() throws InterruptedException {
        if (!running) {
            return;
        }

        running = false;

        if (serverChannel != null) {
            serverChannel.close().sync();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully().sync();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully().sync();
        }

        logger.info("ProxyServer stopped");
    }

    /**
     * Returns {@code true} if the proxy server is currently listening.
     *
     * @return {@code true} when running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Returns the local port this server is bound to.
     *
     * @return local proxy port
     */
    public int getLocalPort() {
        return localPort;
    }

    /**
     * Returns the remote host this proxy forwards traffic to.
     *
     * @return remote host
     */
    public String getRemoteHost() {
        return remoteHost;
    }

    /**
     * Returns the remote port this proxy forwards traffic to.
     *
     * @return remote port
     */
    public int getRemotePort() {
        return remotePort;
    }
}
