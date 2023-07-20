package com.wang.qqframe;

import com.wang.qqserver.service.QQServer;

import java.io.IOException;

/**
 * @author 汪文松
 * @date 2023/7/18 12:51
 * 该类创建一个QQServer对象，启动后台服务
 */
public class QQFrame {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        new QQServer();
    }
}
