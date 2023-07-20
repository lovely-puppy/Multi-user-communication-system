package com.wang.qqserver.service;

import com.wang.qqcommon.Message;
import com.wang.qqcommon.MessageType;
import com.wang.qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 汪文松
 * @date 2023/7/18 11:46
 * 该类对应的一个对象和某个客户端保持通信
 */
public class ServerConnectClientThread extends Thread {
    private Socket socket = null;
    private String userId;//连接到服务端的用户id

    public ServerConnectClientThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {//发送/接收消息

        while (true) {
            System.out.println("服务端和客户端 " + userId + " 保持通信，读取数据...");
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();

                //根据message的类型，做相应的处理
                if (message.getMesType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)) {
                    //客户端要在线用户列表
                    System.out.println(message.getSender() + " 要在线用户列表");
                    String onlineUser = ManageClientThreads.getOnlineUser();
                    //返回message对象
                    Message message2 = new Message();
                    message2.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                    message2.setContent(onlineUser);
                    message2.setGetter(message.getSender());
                    //写入到数据通道，客户端
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message2);
                } else if (message.getMesType().equals(MessageType.MESSAGE_CLIENT_EXIT)) {
                    System.out.println(message.getSender() + " 退出");
                    //将这个客户端对应的线程从集合中移除
                    ManageClientThreads.removeServerConnectClientThread(message.getSender());
                    //关闭连接
                    socket.close();
                    //退出线程
                    break;
                } else if (message.getMesType().equals(MessageType.MESSAGE_COMM_MES)) {

                    HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();
                    if (!hm.containsKey(message.getGetter())) {
                        ManageClientThreads.addOfflineMessage(message.getGetter(), message);
                    } else {
                        ServerConnectClientThread serverConnectClientThread = ManageClientThreads.getServerConnectClientThread(message.getGetter());
                        ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                        oos.writeObject(message);//转发，如果客户不在线，可以保存到数据库中
                    }

                } else if (message.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)) {

                    //遍历管理线程的集合，把所有的线程socket得到,然后把message转发
                    HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();
                    Iterator<String> iterator = hm.keySet().iterator();
                    ObjectOutputStream oos = null;
                    while (iterator.hasNext()) {
                        //取出在线用户的id
                        String onlineUserId = iterator.next();
                        if (!onlineUserId.equals(message.getSender())) {
                            oos = new ObjectOutputStream(hm.get(onlineUserId).getSocket().getOutputStream());
                            oos.writeObject(message);
                        }
                    }

                    HashMap<String, User> validUsers = QQServer.getValidUsers();
                    Set<String> strings = validUsers.keySet();
                    for (String s : strings) {
                        if (!hm.containsKey(s)) {
                            ManageClientThreads.addOfflineMessage(s, message);
                        }
                    }

                } else if (message.getMesType().equals(MessageType.MESSAGE_FILE_MES)) {
                    HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();
                    if (!hm.containsKey(message.getGetter())) {
                        ManageClientThreads.addOfflineMessage(message.getGetter(), message);
                    } else {
                        ObjectOutputStream oos = new ObjectOutputStream(ManageClientThreads.getServerConnectClientThread(message.getGetter()).getSocket().getOutputStream());
                        oos.writeObject(message);
                    }
                } else {
                    System.out.println("其它类型的message, 暂时不处理");
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
