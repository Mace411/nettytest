package com.mace;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 传统nio模式，accept和read不会被阻塞，直接返回null/0，需要不断的轮询，cpu空转
 */
public class NioServer {
    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        try (ServerSocketChannel server = ServerSocketChannel.open()) {
            server.bind(new InetSocketAddress(8080));
            server.configureBlocking(false);
            List<SocketChannel> channels = new ArrayList<>();
            while (true) {
                SocketChannel socketChannel = server.accept();
                if (socketChannel != null) {
                    channels.add(socketChannel);
                    socketChannel.configureBlocking(false);
                }
                channels.forEach(channel -> {
                    try {
                        int read = channel.read(byteBuffer);//往buffer写
                        if (read == -1) {
                            channel.close();//关闭连接，但是从集合中移除这个channel有点麻烦
                        }
                        if (read > 0) {
                            byteBuffer.flip();//切换读
                            byte[] array = new byte[read];
                            byteBuffer.get(array, 0, read);
                            System.err.println(new String(array, Charset.defaultCharset()));
                            byteBuffer.clear();// 切换写
                            byteBuffer.put(array);
                            byteBuffer.flip();// 切换读
                            channel.write(byteBuffer);
                            System.err.println("服务端日志：" + new String(array, Charset.defaultCharset()));
                            byteBuffer.clear();// 切换写
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
