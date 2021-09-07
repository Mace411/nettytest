package com.mace.client;

import com.mace.client.handler.CmdDecoder;
import com.mace.server.cmd.Cmd;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;
import java.util.Scanner;

public class MultiRoomChatClient {

    private final static String IP = "localhost";

    private final static int PORT = 8080;

    public static void main(String[] args) throws InterruptedException {

        NioEventLoopGroup group = new NioEventLoopGroup();

        ChannelFuture channelFuture = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new CmdDecoder());
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress(IP, PORT))
                .sync();
        Channel channel = channelFuture.channel();
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            String next = "connect-";
            System.err.println("输入用户名: ");
            next += scanner.nextLine();
            while (!next.endsWith("close")) {
                channel.writeAndFlush(next);
                next = "chat" + Cmd.DIVIDE + scanner.nextLine();// TODO 不可以空消息
            }
            channel.close();
        }).start();
        ChannelFuture closeFuture = channel.closeFuture();
        closeFuture.addListener(f -> {
            group.shutdownGracefully();
            System.err.println("离开聊天室!!!");
            System.exit(0);
        });
    }

}
