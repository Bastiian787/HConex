package com.hconex.core.proxy;

import com.hconex.config.HabboConfig;
import com.hconex.core.protocol.HabboProtocol;
import com.hconex.logging.PacketLogger;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Netty {@link ChannelInboundHandlerAdapter} that manages a single proxied
 * connection between a local Habbo client and the remote {@code habbo.es} server.
 * <p>
 * When the client connects, this handler opens a corresponding outbound
 * connection to the remote server.  Data arriving from the client is forwarded
 * to the server (and vice-versa) after being intercepted and logged.
 * </p>
 */
public class ProxyHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(ProxyHandler.class);

    private final String remoteHost;
    private final int remotePort;
    private final PacketLogger packetLogger;
    private final HabboProtocol protocol;

    /** Channel connected to the remote habbo.es server. */
    private volatile Channel outboundChannel;

    /**
     * Creates a ProxyHandler that will forward traffic to {@code remoteHost:remotePort}.
     *
     * @param remoteHost remote server hostname
     * @param remotePort remote server port
     */
    public ProxyHandler(String remoteHost, int remotePort) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.packetLogger = PacketLogger.getInstance();
        this.protocol = new HabboProtocol();
    }

    /**
     * Called when a client connects.  Opens the outbound channel to the remote
     * server and registers this connection with {@link ConnectionManager}.
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        final Channel inboundChannel = ctx.channel();
        logger.info("Client connected from {}", inboundChannel.remoteAddress());

        Bootstrap outboundBootstrap = new Bootstrap();
        outboundBootstrap.group(inboundChannel.eventLoop())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.AUTO_READ, false)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, HabboConfig.SERVER_CONNECT_TIMEOUT_MS)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(
                                new OutboundHandler(inboundChannel, packetLogger, protocol));
                    }
                });

        ChannelFuture connectFuture = outboundBootstrap.connect(remoteHost, remotePort);
        outboundChannel = connectFuture.channel();

        connectFuture.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                logger.info("Connected to remote server {}:{}", remoteHost, remotePort);
                ConnectionManager.getInstance().register(inboundChannel, outboundChannel);
                inboundChannel.read();
            } else {
                logger.error("Failed to connect to remote server {}:{}", remoteHost, remotePort,
                        future.cause());
                inboundChannel.close();
            }
        });
    }

    /**
     * Called when the client sends data.  The raw bytes are intercepted,
     * logged, and then forwarded to the remote server.
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (outboundChannel != null && outboundChannel.isActive()) {
            ByteBuf buf = (ByteBuf) msg;
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);

            packetLogger.logOutgoing(bytes);
            protocol.parseOutgoing(bytes);

            outboundChannel.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    ctx.channel().read();
                } else {
                    logger.error("Error forwarding data to remote server", future.cause());
                    future.channel().close();
                }
            });
        } else {
            logger.warn("Outbound channel not available – dropping client data");
        }
    }

    /**
     * Called when the client connection becomes inactive (disconnected).
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.info("Client disconnected from {}", ctx.channel().remoteAddress());
        ConnectionManager.getInstance().unregister(ctx.channel());
        closeOnFlush(outboundChannel);
    }

    /**
     * Called when an uncaught exception occurs in the pipeline.
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Exception in proxy handler for channel {}", ctx.channel(), cause);
        ctx.close();
    }

    /**
     * Flushes any pending writes and then closes the channel.
     *
     * @param ch the channel to close
     */
    static void closeOnFlush(Channel ch) {
        if (ch != null && ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    // =========================================================================
    // Inner class – handles data arriving FROM the remote server
    // =========================================================================

    /**
     * Handles the outbound half of the proxy: data coming from the remote
     * habbo.es server is forwarded back to the local Habbo client.
     */
    static final class OutboundHandler extends ChannelInboundHandlerAdapter {

        private static final Logger outLogger = LogManager.getLogger(OutboundHandler.class);

        private final Channel inboundChannel;
        private final PacketLogger packetLogger;
        private final HabboProtocol protocol;

        OutboundHandler(Channel inboundChannel, PacketLogger packetLogger,
                HabboProtocol protocol) {
            this.inboundChannel = inboundChannel;
            this.packetLogger = packetLogger;
            this.protocol = protocol;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            ctx.read();
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            ByteBuf buf = (ByteBuf) msg;
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);

            packetLogger.logIncoming(bytes);
            protocol.parseIncoming(bytes);

            inboundChannel.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    ctx.channel().read();
                } else {
                    outLogger.error("Error forwarding server data to client", future.cause());
                    future.channel().close();
                }
            });
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            closeOnFlush(inboundChannel);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            outLogger.error("Exception in outbound handler", cause);
            ctx.close();
        }
    }
}
