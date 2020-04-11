package com.server.cloudEn;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class Server {

    private static ExecutorService threadPool = new ThreadPoolExecutor(30,300,60L,
            TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(1000),Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());

    public static void responseData(int port){
        try {
            final ServerSocket serverSocket = new ServerSocket(port);
            //服务端多线程处理，使用线程池(cacheThreadPool)
            while (true) {
                final Socket socket = serverSocket.accept();
                Runnable duty = new Runnable() {
                    public void run() {
                        try {
                            InputStream inputStream = socket.getInputStream();
                            byte[] bytes = new byte[1024];
                            StringBuilder s = new StringBuilder();
                            int len;
                            while ((len = inputStream.read(bytes)) != -1) {
                                s.append(new String(bytes,0, len,"UTF-8"));
                            }
                            System.out.println("get msg from client: " + s);
                            inputStream.close();
                            socket.close();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                threadPool.submit(duty);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ServerProp.initPort("./src/main/java/resources/server.properties");
        responseData(ServerProp.port);
    }
}
