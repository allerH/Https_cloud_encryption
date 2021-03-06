package com.server.cloudEn;

import cn.xjfme.encrypt.utils.Util;
import cn.xjfme.encrypt.utils.sm2.SM2EncDecUtils;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.util.encoders.Hex;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {

    //核心线程数量
    private static int corePoolNum = Runtime.getRuntime().availableProcessors() + 1;
    //计数使用
    private static AtomicInteger count = new AtomicInteger(1);
    //使用线程池进行数据处理发送等操作
    private static final ExecutorService threadPool = new ThreadPoolExecutor(corePoolNum,100,60L,
            TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(1000),Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());

    private static LinkedList<String> strList = new LinkedList<String>();

    //通过认证的列表
    private static HashSet<String> ensureSet = new HashSet<String>();
    /**
     * 服务端接收数据，然后进行拼接操作，再将结果发送给客户端
     * @param port
     */
    public static void responseData(int port){
        System.out.println("服务器启动成功，等待客户端发送数据...");
        try {
            final ServerSocket serverSocket = new ServerSocket(ServerProp.port);
            //服务端多线程处理，使用线程池(cacheThreadPool)
            while (true) {
                final Socket socket = serverSocket.accept();
                System.out.println("----------------------------------------");
                System.out.println("第"+ count.get() +"次连接");
                count.addAndGet(1);
                Runnable duty = new Runnable() {
                    public void run() {
                        try {
                            InputStream inputStream = socket.getInputStream();
                            byte[] bytes = new byte[1024];
                            int idx = 0;
                            int len;
                            while ((len = inputStream.read(bytes,idx, 1024 - idx)) != -1) {
                                String s = new String(bytes, idx, len,"UTF-8");
                                //System.out.println("idx = " + idx + "len = " + len);
                                idx = len;

                                //验证
                                String ip = socket.getInetAddress().getHostAddress();
                                if (!ensureSet.contains(ip)){
                                    System.out.println("开始进行身份认证....");
                                    ensureId(s, ip, socket);
                                    System.out.println("开始接收客户端输入的数据...");
                                    System.out.println("----------------------------------------");
                                    continue;
                                }
                                System.out.println("客户端发送的信息: " + s);
                                if ("#".equals(s)) { //响应客户端
                                    //System.out.println("响应数据发送给客户端");
                                    toClient(socket);
                                    strList = new LinkedList<String>();
                                    break;
                                }

                                strList.add(getPlainText(s));
                            }
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

    /**
     * 验证
     */
    private static boolean ensureId(String cipherText, String ip, Socket socket){
        if (ensureSet.contains(ip)) return true;
        String prik = "0B1CE43098BC21B8E82B5C065EDB534CB86532B1900A49D49F3C53762D2997FA";
        try {
            String plainText = new String(SM2EncDecUtils.decrypt(Util.hexToByte(prik), Util.hexToByte(cipherText)));
            if ("connect to server".equals(plainText)) {
                ensureSet.add(ip);
                //通知客户端
                System.out.println("通知客户端认证成功");
                writeData("OK", socket);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 解密密文数据
     */
    public static String getPlainText(String cipherText){
        String[] s = cipherText.split("\\|");
        if (checkHash(Singleton.getSm4Utils().decryptData_ECB(s[0]), s[1])){
            System.out.println("hash值校验成功");
            return s[0];
        }
        return "";
    }

    /**
     * hash值校验
     */
    public static boolean checkHash(String plainText, String hash){
        byte[] md = new byte[32];
        byte[] msg1 = plainText.getBytes();
        SM3Digest sm3 = new SM3Digest();
        sm3.update(msg1, 0, msg1.length);
        sm3.doFinal(md, 0);
        String s =  new String(Hex.encode(md));
        return s.equals(hash);
    }

    /**
     * 发送数据给客户端
     * @return
     */
    public static void toClient(Socket socket) throws IOException {
        System.out.println("服务端发送给客户端的数据：");
        if (strList == null || strList.size() <= 0) {
            return ;
        }
        //格式化收到的数据
        StringBuilder res = new StringBuilder();
        StringBuilder ans = new StringBuilder();
        for(String s : strList) {
            res.append(s);
            ans.append(s).append(",");
        }
        ans.append(res.toString());
        System.out.println(ans);
        writeData(ans.toString(), socket);
    }

    /**
     * 服务端响应客户端的数据
     * @param msg
     * @param socket
     */
    public static void writeData(String msg, Socket socket) {
        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(msg.getBytes("UTF-8"));
            outputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 主函数，程序入口
     * @param args
     */
    public static void main(String[] args) {
        responseData(ServerProp.port);
        //System.out.println(checkHash("hello ","09ebfd743081201041aaa33bbbb75e6cdc34d0702b583d168b6277d0effb8ba3"));
    }
}
