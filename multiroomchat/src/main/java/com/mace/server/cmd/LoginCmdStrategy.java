package com.mace.server.cmd;

import com.mace.server.RoomManager;
import com.mace.server.user.User;
import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录
 */
//@Handler(cmd = "connect")
public class LoginCmdStrategy implements CmdStrategy {

    @Override
    public void handler(Channel channel, String msg) {
        String name = msg;
        ConcurrentHashMap<Channel, User> users = RoomManager.getUsers();
        users.put(channel, new User(channel, name));
        channel.writeAndFlush(Cmd.CONNECT_SUCCESS);
    }
}
