package com.wang.qqclient.service;

import java.util.HashMap;

/**
 * @author 汪文松
 * @date 2023/7/18 11:13
 * 管理客户端连接到服务器端的线程的类
 */
public class ManageClientConnectServerThread {
    //把多个线程放入到一个集合中，key是用户id，value就是线程
    private static HashMap<String, ClientConnectServerThread> hm = new HashMap<>();

    //将某个线程加入到集合中
    public static void addClientConnectServerThread(String userId, ClientConnectServerThread clientConnectServerThread) {
        hm.put(userId, clientConnectServerThread);
    }

    //通过userId可以得到对应的线程
    public static ClientConnectServerThread getClientConnectServerThread(String userId) {
        return hm.get(userId);
    }

    public static void removeClientConnectServerThread(String userId) {
        hm.remove(userId);
    }
}
