package com.mace.server.cmd;

import io.netty.channel.Channel;

public interface CmdStrategy {

    void handler(Channel channel, String msg);
}
