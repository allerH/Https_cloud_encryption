package com.htj.cloudEnpt;


import cn.xjfme.encrypt.utils.sm4.SM4Utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ClientSocket {

    public ClientSocket(){
        ServerProp.initIpAndPort("./src/main/resources/server.properties");
    }

    public void getInput(){
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        int i = 0;
        String content = "";
        try{
            while (!"#".equals(content)){
                System.out.println("请输入您的第"+ i +"个字符串：");
                content = in.readLine();
                i++;
                String encrptyText = encrptyData(content);
                System.out.println("加密后的密文为：" + encrptyText);
                sendData(encrptyText);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String decrptyData(String encryptData){
        return Singleton.getSm4Utils().decryptData_ECB(encryptData);
    }

    public String encrptyData(String plainText){
        return Singleton.getSm4Utils().encryptData_ECB(plainText);
    }

    public void sendData(String msg) {

        try {
            Socket clientSocket = new Socket(ServerProp.ip, ServerProp.port);
            OutputStream outputStream = clientSocket.getOutputStream();
            outputStream.write(msg.getBytes("UTF-8")); //写出数据
            clientSocket.shutdownOutput(); //数据发送完毕
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ClientSocket one = new ClientSocket();
        one.getInput();

    }
}
