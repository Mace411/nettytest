package com.mace.server.user;


import io.netty.channel.Channel;


public class User {

    private Channel channel;

    private String name;


    public User() {
    }

    public User(Channel channel, String name) {
        this.channel = channel;
        this.name = name;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
