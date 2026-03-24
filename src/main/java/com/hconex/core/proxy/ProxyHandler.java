package com.hconex.core.proxy;

import com.hconex.config.HabboConfig;
import com.hconex.core.packets.Packet;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Handles bidirectional proxy between client and Habbo server
 */
public class ProxyHandler extends SimpleChannelInboundHandler<ByteBuf> {
    
    private final HabboConfig config;
    private Channel serverChannel;
    private boolean connected = false;
    
    public ProxyHandler(HabboConfig config) {
        this.config = config;
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected: " + ctx.channel().remoteAddress());
        
        // Connect to Habbo server
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(ctx.channel().eventLoop())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ServerConnectionHandler(ctx.channel()));
                    }
                });
        
        ChannelFuture future = bootstrap.connect(config.getServerHost(), config.getServerPort());
        future.addListener(f -> {
            if (f.isSuccess()) {
                serverChannel = future.channel();
                connected = true;
                System.out.println("Connected to Habbo server: " + config.getServerHost() + ":" + config.getServerPort());
            } else {
                System.err.println("Failed to connect to server: " + f.cause());
                ctx.close();
            }
        });
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        if (connected && serverChannel != null && serverChannel.isActive()) {
            // Log packet from client to server (OUTGOING)
            byte[] data = new byte[msg.readableBytes()];
            msg.getBytes(msg.readerIndex(), data);
            
            Packet packet = new Packet(0, data, Packet.Direction.OUTGOING);
            System.out.println("Client -> Server: " + packet);
            
            // Forward to server
            serverChannel.writeAndFlush(Unpooled.copiedBuffer(data));
        }
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client disconnected: " + ctx.channel().remoteAddress());
        if (serverChannel != null && serverChannel.isActive()) {
            serverChannel.close();
        }
        connected = false;
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println("Error: " + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
        if (serverChannel != null && serverChannel.isActive()) {
            serverChannel.close();
        }
    }
    
    /**
     * Inner class to handle server connection
     */
    private class ServerConnectionHandler extends SimpleChannelInboundHandler<ByteBuf> {
        
        private final Channel clientChannel;
        
        public ServerConnectionHandler(Channel clientChannel) {
            this.clientChannel = clientChannel;
        }
        
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            // Log packet from server to client (INCOMING)
            byte[] data = new byte[msg.readableBytes()];
            msg.getBytes(msg.readerIndex(), data);
            
            Packet packet = new Packet(0, data, Packet.Direction.INCOMING);
            System.out.println("Server -> Client: " + packet);
            
            // Forward to client
            clientChannel.writeAndFlush(Unpooled.copiedBuffer(data));
        }
        
        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("Server disconnected");
            if (clientChannel.isActive()) {
                clientChannel.close();
            }
        }
        
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            System.err.println("Server error: " + cause.getMessage());
            cause.printStackTrace();
            ctx.close();
            if (clientChannel.isActive()) {
                clientChannel.close();
            }
        }
    }
}
