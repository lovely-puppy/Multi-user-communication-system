package com.wang.qqclient.service;

import com.wang.qqcommon.Message;
import com.wang.qqcommon.MessageType;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * @author 汪文松
 * @date 2023/7/18 11:01
 */
public class ClientConnectServerThread extends Thread {
    //该线程需要持有socket
    private Socket socket;

    //构造器可以接受一个Socket对象
    public ClientConnectServerThread(Socket socket) {
        this.socket = socket;
    }

    //
    @Override
    public void run() {
        //因为线程需要在后台和服务器通信
        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            System.out.println("客户端线程，等待从服务器端发送的消息");
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();//如果服务器没有发送message对象，线程会阻塞在这里
                //判断message的类型，做相应的业务处理
                if (message.getMesType().equals(MessageType.MESSAGE_RET_ONLINE_FRIEND)) {
                    //取出在线列表信息，并显示
                    String[] onlineUsers = message.getContent().split(" ");
                    System.out.println("\n============当前在线用户列表============");
                    for (int i = 0; i < onlineUsers.length; i ++ ) {
                        System.out.println("用户: " + onlineUsers[i]);
                    }

                } else if (message.getMesType().equals(MessageType.MESSAGE_COMM_MES)) {
                    //把从服务器转发的消息输出到控制台
                    System.out.println("\n\n============收到私聊消息==========");
                    System.out.println(message.getSender() + " 对 " + message.getGetter() + " 说: " + message.getContent());
                } else if (message.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)) {
                    System.out.println("\n\n============收到群发消息==========");
                    System.out.println(message.getSender() + " 对大家说: " + message.getContent());
                } else if (message.getMesType().equals(MessageType.MESSAGE_FILE_MES)) {
                    System.out.println("\n\n=============私发文件============");
                    System.out.println(message.getSender() + " 给 " + message.getGetter() + " 发文件: " + message.getSrc() + " 到我的电脑目录: " + message.getDes());
                    //将message的字节数组写入到磁盘
                    FileOutputStream fileOutputStream = new FileOutputStream(message.getDes());
                    fileOutputStream.write(message.getFileBytes());
                    fileOutputStream.close();
                    System.out.println("保存文件成功~");
                }
                else {
                    System.out.println("是其他类型的message, 暂时不处理...");
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public Socket getSocket() {
        return socket;
    }
}
