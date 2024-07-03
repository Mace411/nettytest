package com.mace.bossandworker;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.Scanner;

public class NettyClient {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture channelFuture = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                super.channelRead(ctx, msg);
                                System.err.println("[服务端 - " + LocalDateTime.now() + "]: " + msg);
                            }
                        });
                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                super.write(ctx, msg, promise);
                                System.err.println("[客户端 - " + LocalDateTime.now() + "]: " + msg);
                            }
                        });
                    }
                })
                .connect(new InetSocketAddress(8081))
                .sync();
        Channel channel = channelFuture.channel();
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            String next = "start";
            while (!"close".equals(next)) {
                next = scanner.nextLine();
                channel.writeAndFlush(next);
            }
            channel.close();
        }).start();
        ChannelFuture closeFuture = channel.closeFuture();
        closeFuture.addListener(f -> {
            group.shutdownGracefully();
            System.err.println("客户端关闭!!!");
        });
    }

    public static void test() {

    }
}
