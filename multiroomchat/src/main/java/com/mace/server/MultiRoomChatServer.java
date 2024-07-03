package com.mace.server;

import com.mace.server.handler.CmdDecoder;
import com.mace.server.handler.MessageForwardHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class MultiRoomChatServer {

    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup(1);

        NioEventLoopGroup worker = new NioEventLoopGroup(2);

        final MessageForwardHandler forwardHandler = new MessageForwardHandler();
        final CmdDecoder cmdDecoder = new CmdDecoder();


        ServerBootstrap serverBootstrap = new ServerBootstrap()
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new StringDecoder());
                        pipeline.addLast(cmdDecoder);
                        pipeline.addLast(forwardHandler);
                        pipeline.addLast(new StringEncoder());
                    }
                });
        ChannelFuture channelFuture = serverBootstrap.bind(8080);
        try {
            channelFuture.sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Channel channel = channelFuture.channel();
        ChannelFuture closeFuture = channel.closeFuture();
        closeFuture.addListener(f -> {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        });
    }

}
