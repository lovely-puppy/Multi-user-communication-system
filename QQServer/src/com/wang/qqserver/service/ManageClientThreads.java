package com.wang.qqserver.service;

import com.wang.qqcommon.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 汪文松
 * @date 2023/7/18 11:55
 * 该类用于管理和客户端通信的线程
 */
public class ManageClientThreads {
    private static HashMap<String, ServerConnectClientThread> hm = new HashMap<>();

    //添加线程对象到集合
    public static void addClientThread(String userId, ServerConnectClientThread serverConnectClientThread) {
        hm.put(userId, serverConnectClientThread);
    }

    //根据userid返回线程
    public static ServerConnectClientThread getServerConnectClientThread(String userId) {
        return hm.get(userId);
    }

    //返回在线用户列表
    public static String getOnlineUser() {
        //集合遍历
        Iterator<String> iterator = hm.keySet().iterator();
        String onlineUserList = "";
        while (iterator.hasNext()) {
            onlineUserList += iterator.next().toString() + " ";
        }
        return onlineUserList;
    }

    //从集合中移除某个线程对象
    public static void removeServerConnectClientThread(String userId) {
        hm.remove(userId);
    }

    public static HashMap<String, ServerConnectClientThread> getHm() {
        return hm;
    }

    //储存离线用户消息
    private static ConcurrentHashMap<String, ArrayList<Message>> offlineMessageDb = new ConcurrentHashMap<>();

    //再添加2个静态方法
    public static void addOfflineMessage(String userId, Message message) {
        if (offlineMessageDb.containsKey(userId)) {
            ArrayList<Message> list = offlineMessageDb.get(userId);
            list.add(message);
            offlineMessageDb.put(userId, list);
        } else {
            ArrayList<Message> list = new ArrayList<>();
            list.add(message);
            offlineMessageDb.put(userId, list);
        }
    }

    public static ArrayList<Message> getOfflineMessage(String userId) {
        ArrayList<Message> list = offlineMessageDb.get(userId);
        offlineMessageDb.remove(userId);//读取过一次就删除
        return list;
    }
}
