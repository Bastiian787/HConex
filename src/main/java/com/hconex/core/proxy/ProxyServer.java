package com.hconex.core.proxy;

import com.hconex.config.HabboConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * TCP Proxy Server using Netty
 */
public class ProxyServer {
    
    private final HabboConfig config;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture serverFuture;
    
    public ProxyServer(HabboConfig config) {
        this.config = config;
    }
    
    /**
     * Start the proxy server
     */
    public void start() throws InterruptedException {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ProxyHandler(config));
                        }
                    });
            
            serverFuture = bootstrap.bind(config.getProxyPort()).sync();
            System.out.println("Proxy Server started on port " + config.getProxyPort());
            
        } catch (Exception e) {
            System.err.println("Error starting proxy server: " + e.getMessage());
            e.printStackTrace();
            stop();
            throw e;
        }
    }
    
    /**
     * Wait for the server to stop
     */
    public void waitForShutdown() throws InterruptedException {
        if (serverFuture != null) {
            serverFuture.channel().closeFuture().sync();
        }
    }
    
    /**
     * Stop the proxy server
     */
    public void stop() {
        System.out.println("Shutting down proxy server...");
        if (workerGroup != null) workerGroup.shutdownGracefully();
        if (bossGroup != null) bossGroup.shutdownGracefully();
    }
}
