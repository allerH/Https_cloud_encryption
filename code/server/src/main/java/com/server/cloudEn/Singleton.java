package com.server.cloudEn;

import java.net.ServerSocket;
import java.net.Socket;

public class Singleton {

    private static volatile Socket socket = null;

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
}
