package com.mace.client.handler;

import com.mace.server.cmd.Cmd;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class CmdDecoder extends ChannelInboundHandlerAdapter {

    private PrintHandler printHandler = new PrintHandler();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String[] msgs = ((String) msg).split(Cmd.DIVIDE);// 消息结构：cmd + 发送者 + 消息主体
        printHandler.print(msgs);
        ctx.fireChannelRead(msgs);
    }
}