package com.wang.qqserver.service;

import com.wang.qqcommon.Message;
import com.wang.qqcommon.MessageType;
import com.wang.utils.Utility;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author 汪文松
 * @date 2023/7/20 11:47
 */
public class SendNewsToAllService implements Runnable {

    @Override
    public void run() {

        while (true) {

            System.out.println("请输入服务器要推送的新闻/消息[输入exit退出推送服务]:");
            String news = Utility.readString(60);
            if (news.equals("exit")) {
                break;
            }
            //发送的message
            Message message = new Message();
            message.setSender("服务器");
            message.setMesType(MessageType.MESSAGE_TO_ALL_MES);
            message.setContent(news);
            message.setSendTime(new java.util.Date().toString());
            System.out.println("服务器推送消息给所有人 说:" + news);

            //遍历现在所有通信线程
            HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();
            Iterator<String> iterator = hm.keySet().iterator();
            while (iterator.hasNext()) {
                String onlineUserId = iterator.next();
                ServerConnectClientThread serverConnectClientThread = hm.get(onlineUserId);
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(hm.get(onlineUserId).getSocket().getOutputStream());
                    oos.writeObject(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
