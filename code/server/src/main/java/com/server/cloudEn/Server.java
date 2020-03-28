package com.server.cloudEn;

import java.net.ServerSocket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Server {

    public void responseData(int port){

        try {
            ServerSocket socket = new ServerSocket(port);
            //服务端多线程处理，使用线程池

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        ServerProp.initPort("./src/main/java/resources/server.properties");
    }
}
