package com.htj.cloudEnpt;


import java.io.*;
import java.net.Socket;

public class ClientSocket {

    private Socket socket = null; //单例客户端

    /**
     * 构造器初始化，主要进行ip和port的初始化
     */
    public ClientSocket(){
        ServerProp.initIpAndPort("./src/main/resources/server.properties");
        socket = Singleton.getClientSocket();
    }

    /**
     * 获取用户输入，然后加密，发送到服务端
     */
    public  void getInput() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        int i = 1;
        String content = "";
        try{
            while (!"#".equals(content)){
                System.out.println("请输入您的第"+ i +"个字符串：");
                content = in.readLine();
                i++;
                String encrptyText = encrptyData(content);
                sendData(encrptyText);
            }
            receiveData();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 加密函数，解密客户端从服务端收到的数据
     * @param encryptData
     * @return
     */
    public static String decrptyData(String encryptData){
        return Singleton.getSm4Utils().decryptData_ECB(encryptData);
    }

    /**
     * 加密函数，包含结尾符号处理
     * @param plainText
     * @return
     */
    public static String encrptyData(String plainText){
        if ("#".equals(plainText)){
            return "#";
        }
        String encrptyText =  Singleton.getSm4Utils().encryptData_ECB(plainText);
        System.out.println("加密后的密文为：" + encrptyText);
        return encrptyText;
    }

    /**
     * 通过socket发送数据
     * @param msg
     */
    public void sendData(String msg) {
        try {
            //Socket socket = new Socket(ServerProp.ip, ServerProp.port); //每次传送数据一个新的tcp连接
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(msg.getBytes("UTF-8")); //写出数据
            outputStream.flush();
            //socket.shutdownOutput();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 接收服务端传递来的数据
     */
    public void receiveData(){
        System.out.println("----------------------------------------");
        System.out.println("开始接收服务端返回的数据...");
        try {
            //Socket socket = new Socket(ServerProp.ip, ServerProp.port);
            InputStream in = socket.getInputStream();
            byte[] buffer = new byte[1024];
            StringBuilder s = new StringBuilder();
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
               s.append(new String(buffer, 0, len, "UTF-8"));
            }
            showData(s.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 对服务端的数据解密展示
     */
    public void showData(String s) {
        String[] strs = s.split(",");
        String encrptyText = strs[strs.length - 1];
        System.out.println("总的解密的结果是：" + decrptyData(encrptyText));
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < strs.length - 1; i++){
            res.append(decrptyData(strs[i]));
        }
        System.out.println("分开解密的结果是：" + res.toString());
    }

    /**
     * 主函数，程序入口
     * @param args
     */
    public static void main(String[] args) {
       ClientSocket one = new ClientSocket();
       one.getInput();
    }
}
