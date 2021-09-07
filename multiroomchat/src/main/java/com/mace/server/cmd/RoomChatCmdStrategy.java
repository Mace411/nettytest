package com.mace.server.cmd;

import com.mace.server.RoomManager;
import com.mace.server.user.User;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聊天
 */
//@Handler(cmd = "chat")
public class RoomChatCmdStrategy implements CmdStrategy {
    @Override
    public void handler(Channel channel, String msg) {
        ConcurrentHashMap<Channel, User> users = RoomManager.getUsers();
        String senderName = users.get(channel).getName();
        Iterator<Map.Entry<Channel, User>> iterator = users.entrySet().iterator();
        while (iterator.hasNext()) {
            Channel otherChannel = iterator.next().getKey();
            // 断开了连接的不发，移除掉引用
            if (!channel.isActive()) {
                iterator.remove();
                continue;
            }
//            // 不给自己发
//            if (channel.equals(otherChannel)) {
//                continue;
//            }
            otherChannel.writeAndFlush(Cmd.CHAT + Cmd.DIVIDE + senderName + Cmd.DIVIDE + msg);
        }
    }
}
