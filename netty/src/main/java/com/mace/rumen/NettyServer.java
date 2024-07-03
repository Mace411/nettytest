package com.mace.rumen;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

public class NettyServer {

    static void t() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
//
//        for (Integer i : list) {
//            if (i == 1) {
//                list.remove(i);
//            }
//        }

//        for (int i = 0; i < list.size(); i++) {
//            if (list.get(i) == 1) {
//                list.remove(i);
//            }
//        }
        int[] ints = {1, 2, 3};
        System.err.println(list.size());
        System.err.println(list.get(4));
        System.err.println(ints[4]);
    }

    public static void main(String[] args) {
        t();
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel nioSocketChannel) {
                        nioSocketChannel.pipeline()
                                .addLast(new StringDecoder())
                                .addLast(new SimpleChannelInboundHandler<String>() {
                                    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                        System.err.println(msg);
                                    }
                                });
                    }
                }).bind(8081);

        // 1、启动器，负责装配netty组件，启动服务器
//        new ServerBootstrap()
//                // 2、创建 NioEventLoopGroup，可以简单理解为 线程池 + Selector
//                .group(new NioEventLoopGroup())
//                // 3、选择服务器的 ServerSocketChannel 实现
//                .channel(NioServerSocketChannel.class)
//                // 4、child 负责处理读写，该方法决定了 child 执行哪些操作
//                // ChannelInitializer 处理器（仅执行一次）
//                // 它的作用是待客户端SocketChannel建立连接后，执行initChannel以便添加更多的处理器
//                .childHandler(new ChannelInitializer<NioSocketChannel>() {
//                    @Override
//                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
//                        // 5、SocketChannel的处理器，使用StringDecoder解码，ByteBuf=>String
//                        nioSocketChannel.pipeline().addLast(new StringDecoder());
//                        // 6、SocketChannel的业务处理，使用上一个处理器的处理结果
//                        nioSocketChannel.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
//                            @Override
//                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
//                                System.out.println(s);
//                            }
//                        });
//                    }
//                    // 7、ServerSocketChannel绑定8080端口
//                }).bind(8081);
    }


    static void test() {
        Object object = new Object();
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        Future<?> submit = scheduledExecutorService.submit(() -> {
//            try {
//                System.err.println("任务1");
//                object.wait();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            int i = 1;
            while (true) {
                if (i == Integer.MAX_VALUE) {
                    i = 1;
                }
                i++;
            }
        });
        try {
            Thread.currentThread().sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.err.println("打断");
        submit.cancel(true);
        scheduledExecutorService.submit(() -> System.err.println("任务2"));
    }

}