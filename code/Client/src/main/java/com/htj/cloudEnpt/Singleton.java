package com.htj.cloudEnpt;

import cn.xjfme.encrypt.utils.sm2.SM2EncDecUtils;
import cn.xjfme.encrypt.utils.sm3.SM3;
import cn.xjfme.encrypt.utils.sm4.SM4Utils;

import java.net.Socket;

public class Singleton {
    public static final String publicKey ="04BB34D657EE7E8490E66EF577E6B3CEA28B739511E787FB4F71B7F38F241D87F18A5A93DF74E90FF94F4EB907F271A36B295B851F971DA5418F4915E2C1A23D6E";
    public static final String privatekey = "0B1CE43098BC21B8E82B5C065EDB534CB86532B1900A49D49F3C53762D2997FA";
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
