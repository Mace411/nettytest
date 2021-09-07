package com.mace;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

public class NioClient {


    public static void main(String[] args) {
        SocketChannel socketChannel = null;
        try {
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress(8080));
            ByteBuffer byteBuffer = ByteBuffer.allocate(16);
            byteBuffer.put("hello javanio".getBytes(Charset.defaultCharset()));
            byteBuffer.flip();//切换读
            socketChannel.write(byteBuffer);
            Scanner scanner = new Scanner(System.in);
            String next = "start";
            while (!"close".equals(next)) {
                byteBuffer.clear();// 切换写
                int read = socketChannel.read(byteBuffer);
                if (read > 0) {
                    byte[] array = new byte[read];
                    byteBuffer.flip();// 切换读
                    byteBuffer.get(array, 0, read);
                    System.err.println("服务端响应: " + new String(array, Charset.defaultCharset()));
                    byteBuffer.clear();// 切换写
                    next = scanner.nextLine();
                    array = next.getBytes(Charset.defaultCharset());
                    byteBuffer.put(array);
                    System.err.println("客户端日志：" + next);
                    byteBuffer.flip();// 切换读
                    socketChannel.write(byteBuffer);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
