package com.mace.server.utils;

import com.mace.server.cmd.CmdStrategy;
import com.mace.server.cmd.LoginCmdStrategy;
import com.mace.server.cmd.RoomChatCmdStrategy;
import com.sun.istack.internal.Nullable;

import java.util.HashMap;
import java.util.Map;

public class StragetyContext {

    private final static Map<String, CmdStrategy> handlerMap = new HashMap<>();

    enum Cmd {
        CONNECT(com.mace.server.cmd.Cmd.CONNECT, new LoginCmdStrategy()),

        CHAT(com.mace.server.cmd.Cmd.CHAT, new RoomChatCmdStrategy()),
        ;

        private String cmd;
        private CmdStrategy handler;

        Cmd(String cmd, CmdStrategy handler) {
            this.cmd = cmd;
            this.handler = handler;
        }

    }

    static {
        for (Cmd value : Cmd.values()) {
            handlerMap.put(value.cmd, value.handler);
        }
    }

    @Nullable
    public static CmdStrategy getStrategy(String cmd) {
        return handlerMap.get(cmd);
    }


}
