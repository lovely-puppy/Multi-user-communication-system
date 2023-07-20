package com.wang.qqclient.service;

import com.wang.qqcommon.Message;
import com.wang.qqcommon.MessageType;
import com.wang.qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @author 汪文松
 * @date 2023/7/18 10:47
 * 完成用户登录验证和用户注册等功能
 */
public class UserClientService {
    //可能在其他地方要使用用户信息
    private User u = new User();
    //socket在其他地方也可能会使用，故将其做为属性
    private Socket socket = null;

    //根据userId和pwd去服务器判断用户是否合法
    public boolean checkUser(String userId, String pwd) throws IOException, ClassNotFoundException {
        boolean b = false;
        //创建user对象
        u.setUserId(userId);
        u.setPwd(pwd);
        //连接到服务器
        socket = new Socket(InetAddress.getByName("127.0.0.1"), 9999);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(u);//发送user对象

        //读取从服务端回送的Message对象
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        Message ms = (Message) ois.readObject();

        if (ms.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCEED)) {//登陆成功

            //创建一个和服务器端保持通信的线程 -> 创建一个类ClientConnectServerThread
            ClientConnectServerThread clientConnectServerThread = new ClientConnectServerThread(socket);
            //启动客户端线程
            clientConnectServerThread.start();
            //为了客户端的扩展，将线程放入到一个集合中
            ManageClientConnectServerThread.addClientConnectServerThread(userId, clientConnectServerThread);

            b = true;
        } else {//登陆失败
            //不能启动和服务器端通信的线程，关闭socket
            socket.close();
        }

        return b;
    }

    //向服务器端请求在线用户列表
    public void onlineFriendList() throws IOException {

        //发送一个message
        Message message = new Message();
        message.setSender(u.getUserId());
        message.setMesType(MessageType.MESSAGE_GET_ONLINE_FRIEND);

        //发送给服务器
        //得到当前线程的socket 对应的ObjectOutputStream对象
        ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(u.getUserId()).getSocket().getOutputStream());
        oos.writeObject(message);
    }

    //向服务端发送一个退出的message对象
    public void logout() throws IOException {
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
        message.setSender(u.getUserId());

        ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(u.getUserId()).getSocket().getOutputStream());
        oos.writeObject(message);
        System.out.println(u.getUserId() + " 退出了系统");
        System.exit(0);
    }
}
