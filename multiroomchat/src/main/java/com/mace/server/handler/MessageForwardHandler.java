package com.mace.server.handler;

import com.mace.server.RoomManager;
import com.mace.server.cmd.Cmd;
import com.mace.server.cmd.CmdStrategy;
import com.mace.server.user.User;
import com.mace.server.utils.StragetyContext;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ConcurrentHashMap;

@ChannelHandler.Sharable
public class MessageForwardHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String[] msgs = (String[]) msg;
        CmdStrategy strategy = StragetyContext.getStrategy(msgs[0]);
        strategy.handler(ctx.channel(), msgs[1]);
        print(ctx, msgs);
        ctx.fireChannelRead(msg);
    }

    public void print(ChannelHandlerContext ctx, String[] msg) {
        ConcurrentHashMap<Channel, User> users = RoomManager.getUsers();
        User user = users.get(ctx.channel());
        System.err.println(ctx.channel() + " " + msg[0] + Cmd.DIVIDE + user.getName() + Cmd.DIVIDE + msg[1]);
    }
}
