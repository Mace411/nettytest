package com.mace.bossandworker;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 *  boss负责accept，worker负责读写，business负责业务操作
 */
public class NettyServer {

    private final static int THREAD_NUM = 3;

    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup worker = new NioEventLoopGroup(2);
        NioEventLoopGroup[] business = new NioEventLoopGroup[THREAD_NUM];
        for (int i = 0; i < business.length; i++) {
            business[i] = new NioEventLoopGroup(1);
        }
        new ServerBootstrap()
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                System.err.println("[客户端 - " + LocalDateTime.now() + "]: " + msg);
                                ctx.fireChannelRead(msg);
                            }
                        });
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                final Channel channel = ctx.channel();
                                final String message = (String) msg;
                                NioEventLoopGroup next = business[msg.hashCode() % THREAD_NUM];
                                next.submit(() -> {
                                    System.err.println("业务执行......");
//                                    try {
//                                        TimeUnit.SECONDS.sleep(2);
                                        channel.writeAndFlush(message);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
                                });
                            }
                        });
                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                super.write(ctx, msg, promise);
                                System.err.println("[服务端 - " + LocalDateTime.now() + "]: " + msg);
                            }
                        });
                    }
                })
                .bind(8081);
    }
}
