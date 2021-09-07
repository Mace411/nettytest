package com.mace.client.handler;

import com.mace.server.cmd.Cmd;

import java.time.LocalDateTime;

public class PrintHandler {

    public void print(String[] msg) {
        if (msg[0].equals(Cmd.CONNECT_SUCCESS)) {
            System.err.println("连接成功!!!");
            System.err.println();
        } else {
            System.err.println(msg[1] + " " + LocalDateTime.now());
            System.err.println(msg[2]);
        }
    }
}
