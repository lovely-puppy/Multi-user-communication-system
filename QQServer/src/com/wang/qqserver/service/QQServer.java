package com.wang.qqserver.service;

import com.wang.qqcommon.Message;
import com.wang.qqcommon.MessageType;
import com.wang.qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 汪文松
 * @date 2023/7/18 11:33
 * 服务端，在监听端口，等待客户端的连接并保持通信
 */
public class QQServer {

    private ServerSocket serverSocket = null;
    //创建一个集合，存放多个用户
    private static HashMap<String, User> validUsers = new HashMap<>();

    public static HashMap<String, User> getValidUsers() {
        return validUsers;
    }

    //在静态代码块初始化用户
    static {
        validUsers.put("100", new User("100", "123456"));
        validUsers.put("200", new User("200", "123456"));
        validUsers.put("300", new User("300", "123456"));
        validUsers.put("admin", new User("admin", "123456"));
        validUsers.put("至尊宝", new User("至尊宝", "123456"));
        validUsers.put("紫霞仙子", new User("紫霞仙子", "123456"));
    }

    //验证用户是否有效的方法
    private boolean checkUser(String userId, String pwd) {
        User user = validUsers.get(userId);
        if (user == null) {
            return false;
        }
        if (!user.getPwd().equals(pwd)) {
            return false;
        }
        return true;
    }

    public QQServer() throws IOException, ClassNotFoundException {
        //端口可以写在一个配置文件中
        System.out.println("服务器在9999端口监听...");
        //启动推送服务
        new Thread(new SendNewsToAllService()).start();
        serverSocket = new ServerSocket(9999);

        while (true) {//和某个客户端连接后会继续监听
            Socket socket = serverSocket.accept();//如果没有客户端连接就会阻塞在这

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            User u = (User) ois.readObject();//读取客户端发送的user对象
            //得到socket关联的输出流
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

            //创建一个message对象回复客户端
            Message message = new Message();
            //验证
            if (checkUser(u.getUserId(), u.getPwd())) {//登陆成功

                message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                //将message对象回复给客户端
                oos.writeObject(message);
                //创建一个线程和客户端保持通讯，该线程需要持有socket对象
                ServerConnectClientThread serverConnectClientThread = new ServerConnectClientThread(socket, u.getUserId());

                //向该上线用户发送之前的离线消息
                ArrayList<Message> offlineMessage = ManageClientThreads.getOfflineMessage(u.getUserId());
                if (offlineMessage != null) {
                    for (int i = 0; i < offlineMessage.size(); i ++ ) {
                        oos = new ObjectOutputStream(socket.getOutputStream());
                        oos.writeObject(offlineMessage.get(i));
                    }
                }

                //启动该线程
                serverConnectClientThread.start();
                //把该线程对象放入到一个集合当中
                ManageClientThreads.addClientThread(u.getUserId(), serverConnectClientThread);

            } else {
                System.out.println("用户 id="+ u.getUserId()+" 密码="+u.getPwd()+ " 登陆失败");
                message.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                oos.writeObject(message);
                socket.close();
            }
        }
        //如果服务端退出while,服务端不在监听
//        serverSocket.close();
    }
}
