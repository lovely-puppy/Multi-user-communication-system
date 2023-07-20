package com.wang.qqcommon;

/**
 * @author 汪文松
 * @date 2023/7/18 9:52
 */
public interface MessageType {
    String MESSAGE_LOGIN_SUCCEED = "1";//表示登陆成功
    String MESSAGE_LOGIN_FAIL = "2";//登录失败
    String MESSAGE_COMM_MES = "3";//普通信息包
    String MESSAGE_GET_ONLINE_FRIEND = "4";//要求返回在线用户列表
    String MESSAGE_RET_ONLINE_FRIEND = "5";//返回在线用户列表
    String MESSAGE_CLIENT_EXIT = "6";//客户端请求退出
    String MESSAGE_TO_ALL_MES = "7";//群发消息
    String MESSAGE_FILE_MES = "8";//发送文件
}
