package com.mace;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class JavaNioClient {

    public static void main(String[] args) {
        SocketChannel client = null;
        ByteBuffer readBuffer = ByteBuffer.allocate(16);
        try {
            client = SocketChannel.open();
            client.connect(new InetSocketAddress(8080));
            client.configureBlocking(false);
            Selector selector = Selector.open();
            client.register(selector, SelectionKey.OP_READ, readBuffer);
            String next = "start";
            Scanner scanner = new Scanner(System.in);
            while (!"close".equals(next)) {
                next = scanner.nextLine();
                ByteBuffer writeBuffer = ByteBuffer.allocate(16);
                writeBuffer.put(next.getBytes(Charset.defaultCharset()));
                writeBuffer.flip();// 切换读
                client.write(writeBuffer);
                if (writeBuffer.hasRemaining()) {
                    client.register(selector, SelectionKey.OP_WRITE, writeBuffer);
                }
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();
                    SocketChannel channel = (SocketChannel) selectionKey.channel();
                    if (selectionKey.isReadable()) {
                        readBuffer.clear();
                        int read = channel.read(readBuffer);
                        if (read > 0) {
                            byte[] array = new byte[read];
                            readBuffer.flip();// 切换读
                            readBuffer.get(array, 0, read);
                            System.err.println("服务端：" + new String(array, Charset.defaultCharset()));
                        }
                    }
                    if (selectionKey.isWritable()) {
                        ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
                        channel.write(buffer);
                        if (!writeBuffer.hasRemaining()) {
                            selectionKey.attach(null);
                            selectionKey.interestOps(0);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
