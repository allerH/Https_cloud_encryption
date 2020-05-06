package com.htj.cloudEnpt;

import cn.xjfme.encrypt.utils.sm4.SM4Utils;

import java.net.Socket;

public class Singleton {
    private static volatile SM4Utils sm4Utils = null;
    private static volatile Socket clientSocket = null;

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

    public static Socket getClientSocket() {
        if (clientSocket == null) {
            synchronized (Singleton.class) {
                if (clientSocket == null) {
                    try {
                        clientSocket = new Socket(ServerProp.ip, ServerProp.port);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
                return clientSocket;
            }
        }
        return clientSocket;
    }
}
