package com.server.cloudEn;

import cn.xjfme.encrypt.utils.sm4.SM4Utils;

import java.net.ServerSocket;
import java.net.Socket;

public class Singleton {

    private static volatile Socket socket = null;
    private static volatile SM4Utils sm4Utils = null;

    public static Socket getSocket(){
        if (socket == null) {
            synchronized (Singleton.class) {
                if (socket == null){
                    try {
                        ServerSocket serverSocket = new ServerSocket(ServerProp.port);
                        socket = serverSocket.accept();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return socket;
            }
        }
        return socket;
    }

    public static SM4Utils getSm4Utils(){
        if (sm4Utils == null){
            synchronized (Singleton.class){
                if (sm4Utils == null) {
                    sm4Utils = new SM4Utils();
                }
                return sm4Utils;
            }
        }
        return sm4Utils;
    }

}
