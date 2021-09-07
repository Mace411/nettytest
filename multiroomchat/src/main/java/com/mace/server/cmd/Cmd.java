package com.mace.server.cmd;

public interface Cmd {
    // 暂时使用简单的分隔符作为消息解析的依据
    String DIVIDE = "-";

    String CONNECT = "connect";

    String CONNECT_SUCCESS = "connect_success";

    String CHAT = "chat";

}

