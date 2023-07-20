package com.wang.qqclient.view;

import com.wang.qqclient.service.FileClientService;
import com.wang.qqclient.service.MessageClientServer;
import com.wang.qqclient.service.UserClientService;
import com.wang.qqclient.utils.Utility;

import java.io.IOException;

/**
 * @author 汪文松
 * @date 2023/7/18 9:59
 */
public class QQView {
    private boolean loop = true;//控制是否显示菜单
    private boolean loop2 = true;//二级菜单
    private String key = "";//接受用户键盘输入
    private UserClientService userClientService = new UserClientService();//用于登录服务器
    private MessageClientServer messageClientServer = new MessageClientServer();//用于用户私聊/群聊
    private FileClientService fileClientService = new FileClientService();//传文件

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        new QQView().mainView();
        System.out.println("客户端退出系统");
    }

    //显示主菜单
    private void mainView() throws IOException, ClassNotFoundException {
        while (loop) {
            System.out.println("================欢迎登录网络通讯系统================");
            System.out.println("\t\t 1 登陆系统");
            System.out.println("\t\t 9 退出系统");
            System.out.print("请输入你的选择: ");
            key = Utility.readString(1);

            switch(key) {
                case "1" -> {
                    System.out.print("请输入用户号: ");
                    String userId = Utility.readString(50);
                    System.out.print("请输入密 码: ");
                    String pwd = Utility.readString(50);
                    //需要到服务端去验证该用户是否合法
                    //编写一个类UserClientService[用户注册/登录]
                    if (userClientService.checkUser(userId, pwd)) {//还没写完,先把逻辑写通
                        System.out.println("================欢迎(用户"+userId+")成功登录================");
                        loop2 = true;
                        //进入二级菜单
                        while (loop2) {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            System.out.println("\n================网络通信系统二级菜单(用户"+userId+")================");
                            System.out.println("\t\t 1 显示在线用户列表");
                            System.out.println("\t\t 2 群发消息");
                            System.out.println("\t\t 3 私聊消息");
                            System.out.println("\t\t 4 发送文件");
                            System.out.println("\t\t 9 注销当前用户");

                            System.out.print("请输入你的选择: ");
                            key = Utility.readString(1);

                            switch (key) {
                                case "1" -> {
                                    userClientService.onlineFriendList();
//                                    System.out.println("显示在线用户列表");
                                }
                                case "2" -> {
                                    System.out.print("请输入想对大家说的话:");
                                    String s = Utility.readString(100);
                                    messageClientServer.sendMessageToAll(s, userId);
//                                    System.out.println("群发消息");
                                }
                                case "3" -> {
                                    System.out.print("请输入您想聊天的用户号: ");
                                    String getterId = Utility.readString(50);
                                    System.out.print("请输入想说的话: ");
                                    String content = Utility.readString(100);
                                    //将私聊的信息发送给服务端
                                    messageClientServer.sendMessageToOne(content, userId, getterId);

//                                    System.out.println("私聊消息");
                                }
                                case "4" -> {
                                    System.out.print("请输入你想发送文件的用户:");
                                    String getterId = Utility.readString(50);
                                    System.out.print("请输入发送文件的路径(d:\\xx.png):");
                                    String src = Utility.readString(100);
                                    System.out.print("请输入把文件发送到对方电脑对应的路径(d:\\xx.png):");
                                    String des = Utility.readString(100);

                                    //传文件
                                    fileClientService.sendToOne(src, des, userId, getterId);
//                                    System.out.println("发送文件");
                                }
                                case "9" -> {
                                    //调用方法，向服务器发送一个退出的message对象
                                    userClientService.logout();
                                    loop2 = false;
                                }
                            }
                        }
                    } else {//登陆服务器失败
                        System.out.println("===============用户名或密码错误============\n");
                    }
                }
                case "9" -> {
                    loop = false;
                }
            }
        }
    }
}
