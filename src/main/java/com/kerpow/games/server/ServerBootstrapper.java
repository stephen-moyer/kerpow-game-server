package com.kerpow.games.server;

import com.kerpow.games.server.net.KerpowGameDecoder;
import com.kerpow.games.server.net.KerpowGameEncoder;
import com.kerpow.games.server.net.KerpowServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;

public class ServerBootstrapper {

    public ServerBootstrapper() {

    }

    public ServerBootstrap bootstrap(Server server) {
        ServerInfo serverInfo = server.getServerInfo();
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {

                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new KerpowGameDecoder());
                        pipeline.addLast(new KerpowGameEncoder(server.getPacketHandler()));
                        pipeline.addLast(new KerpowServerHandler(server));
                    }

                })
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true);

        System.out.println("Listening on " + serverInfo.port);
        bootstrap.bind(new InetSocketAddress(serverInfo.port));
        return bootstrap;
    }


}
