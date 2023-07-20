package com.wang.qqclient.service;

import com.wang.qqcommon.Message;
import com.wang.qqcommon.MessageType;

import java.io.*;

/**
 * @author 汪文松
 * @date 2023/7/20 10:34
 * 完成文件的传输
 */
public class FileClientService {

    public void sendToOne(String src, String des, String senderId, String getterId) {

        //读取src文件
        Message message = new Message();
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setMesType(MessageType.MESSAGE_FILE_MES);
        message.setSrc(src);
        message.setDes(des);

        //读取文件
        FileInputStream fileInputStream = null;
        byte[] fileBytes = new byte[(int) new File(src).length()];

        try {
            fileInputStream = new FileInputStream(src);
            fileInputStream.read(fileBytes);
            //将文件对应的字节数组设置message
            message.setFileBytes(fileBytes);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        System.out.println("\n" + message.getSender() + " 给 " + getterId + " 发送文件: " + src + " 到对方电脑的目录 " + des);
        //发送
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
