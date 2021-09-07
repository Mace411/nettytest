package com.mace;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * 使用selector 多路复用
 */
public class JavaNioServer {

    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        ServerSocketChannel server = null;
        try {
            server = ServerSocketChannel.open();
            server.bind(new InetSocketAddress(8080));
            server.configureBlocking(false);
            Selector selector = Selector.open();
            server.register(selector, SelectionKey.OP_ACCEPT);// 注册到selector上
            while (true) {
                selector.select();// 没有事件，线程会被阻塞，cpu不会空转
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();// 移除处理过的事件，不然会被当作没有处理。
                    if (selectionKey.isAcceptable()) {
                        ServerSocketChannel serverChannel = (ServerSocketChannel) selectionKey.channel();
                        try {
                            SocketChannel socketChannel = serverChannel.accept();
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector, SelectionKey.OP_READ);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else if (selectionKey.isReadable()) { // 要用else if， 不然事件被取消掉了之后，下面的判断会报错CancelledKeyException
                        SocketChannel channel = (SocketChannel) selectionKey.channel();
                        byteBuffer.clear();
                        try {
                            int read = channel.read(byteBuffer);
                            if (read > 0) {
                                byte[] array = new byte[read];
                                byteBuffer.flip();
                                byteBuffer.get(array, 0, read);
                                byteBuffer.clear();
                                System.err.println("客户端：" + new String(array, Charset.defaultCharset()));
                                ByteBuffer writeBuffer = ByteBuffer.wrap(array);//返回的是读模式
                                channel.write(writeBuffer);
                                if (writeBuffer.hasRemaining()) {
                                    channel.register(selector, SelectionKey.OP_WRITE, writeBuffer);
                                }
                            }
                            if (read == -1) {
                                selectionKey.cancel();// 需要把事件取消掉，不然事件会继续来
                                channel.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else if (selectionKey.isWritable()) {
                        SocketChannel channel = (SocketChannel) selectionKey.channel();
                        ByteBuffer attachment = (ByteBuffer) selectionKey.attachment();
                        if (attachment.hasRemaining()) {
                            try {
                                channel.write(attachment);
                                if (!attachment.hasRemaining()) {
                                    selectionKey.attach(null);
                                    selectionKey.interestOps(0);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


}
