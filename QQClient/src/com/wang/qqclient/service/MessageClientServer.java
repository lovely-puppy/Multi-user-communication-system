package com.wang.qqclient.service;

import com.wang.qqcommon.Message;
import com.wang.qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @author 汪文松
 * @date 2023/7/20 9:14
 * 用于向服务端提供消息相关的方法
 */
public class MessageClientServer {

    public void sendMessageToOne(String content, String senderId, String getterId) throws IOException {
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_COMM_MES);
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setContent(content);
        message.setSendTime(new java.util.Date().toString());
        System.out.println(senderId + " 对 " + getterId + " 说: " + content);

        //发送给服务端
        ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
        oos.writeObject(message);
    }

    public void sendMessageToAll(String content, String senderId) throws IOException {
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_TO_ALL_MES);
        message.setContent(content);
        message.setSender(senderId);
        message.setSendTime(new java.util.Date().toString());
        System.out.println(senderId + " 对大家说: " + content);

        ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
        oos.writeObject(message);
    }
}
