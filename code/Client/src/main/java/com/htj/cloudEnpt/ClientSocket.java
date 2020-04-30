package com.htj.cloudEnpt;

import java.io.OutputStream;
import java.net.Socket;

public class ClientSocket {

    public void sendData(String ip, int port, String message) {
        try {
            Socket clientSocket = new Socket(ip, port);
            OutputStream outputStream = clientSocket.getOutputStream();
            outputStream.write(message.getBytes("UTF-8")); //写出数据
            clientSocket.shutdownOutput(); //数据发送完毕
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ServerProp.initIpAndPort("./src/main/resources/server.properties");
        new ClientSocket().sendData(ServerProp.ip, ServerProp.port, "hello world");

    }
}
