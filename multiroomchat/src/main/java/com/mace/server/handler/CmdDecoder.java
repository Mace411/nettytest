package com.mace.server.handler;

import com.mace.server.cmd.Cmd;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class CmdDecoder extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String[] msgs = ((String) msg).split(Cmd.DIVIDE);// 消息结构：cmd + 消息主体
        ctx.fireChannelRead(msgs);
    }

    public static void main(String[] args) {
        String a = "";
        String[] split = a.split("2");
        System.err.println(split.length);
    }
}
