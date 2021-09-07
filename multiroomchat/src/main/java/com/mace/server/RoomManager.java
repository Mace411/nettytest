package com.mace.server;

import com.mace.server.user.User;
import io.netty.channel.Channel;
import lombok.Getter;

import java.util.concurrent.ConcurrentHashMap;

@Getter
public class RoomManager {
    private final static ConcurrentHashMap<Channel, User> users = new ConcurrentHashMap<>();

//    private final static Set<String> userMap = new HashSet<>();
//
//    private final static String[] userNames = {"二狗", "张三", "李四", "王五", "赵六"};
//
//    static {
//        Collections.addAll(userMap, userNames);
//    }

    public static ConcurrentHashMap<Channel, User> getUsers() {
        return users;
    }

//    public static Set<String> getUserMap() {
//        return userMap;
//    }
}
