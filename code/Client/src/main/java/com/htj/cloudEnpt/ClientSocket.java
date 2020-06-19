package com.htj.cloudEnpt;


import cn.xjfme.encrypt.utils.Util;
import cn.xjfme.encrypt.utils.sm2.SM2EncDecUtils;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.util.encoders.Hex;


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
    public void getInput() {
        //身份验证
        ensureId("connect to server");
        if (!isSuccess()){
            System.out.println("认证失败!!!");
            return;
        }
        System.out.println("身份认证成功!!!");
        System.out.println("开始接收用户输入数据...");
        System.out.println("----------------------------------------");
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
     * sm4解密函数，解密客户端从服务端收到的数据
     * @param encryptData
     * @return
     */
    public static String decrptySM4(String encryptData) throws Exception{
        //return Singleton.getSm4Utils().decryptData_CBC(encryptData);
        return Singleton.getSm4Utils().decryptData_ECB(encryptData);
        //return new String(SM2EncDecUtils.decrypt(Util.hexToByte(Singleton.privatekey), Util.hexToByte(encryptData)));
    }

    /**
     * sm4算法加密函数，加密客户端输入
     * @param plainText
     * @return
     */
    public static String encrptySM4(String plainText) throws Exception {
        //return Singleton.getSm4Utils().encryptData_CBC(plainText);
        return Singleton.getSm4Utils().encryptData_ECB(plainText);
        //return new String(SM2EncDecUtils.encrypt(Util.hexToByte(Singleton.publicKey), plainText.getBytes()));
    }

    public static String getHash(String plainText){
        byte[] md = new byte[32];
        byte[] msg1 = plainText.getBytes();
        SM3Digest sm3 = new SM3Digest();
        sm3.update(msg1, 0, msg1.length);
        sm3.doFinal(md, 0);
        return new String(Hex.encode(md));
    }

    /**
     * 加密控制
     * @param plainText
     * @return
     */
    public static String encrptyData(String plainText) throws Exception {
        if ("#".equals(plainText)){
            return "#";
        }
        //生成hash值
        String hash = getHash(plainText);
        System.out.println("生成的hash数值为：" + hash);
        //sm4加密
        String encrptyText = encrptySM4(plainText);
        System.out.println("加密后的密文为：" + encrptyText);
        return encrptyText + "|" + hash;
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
    public void showData(String s) throws Exception {
        String[] strs = s.split(",");
        String encrptyText = strs[strs.length - 1];
        System.out.println("总的解密的结果是：" + decrptySM4(encrptyText));
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < strs.length - 1; i++){
            res.append(decrptySM4(strs[i]));
        }
        System.out.println("分开解密的结果是：" + res.toString());
    }
    /**
     * 身份认证
     */
    private void ensureId(String plainText){
        System.out.println("开始进行身份认证...");
        //使用公钥加密某个字符串
        try {
            byte[] sourceData = plainText.getBytes();
            String pubk ="04BB34D657EE7E8490E66EF577E6B3CEA28B739511E787FB4F71B7F38F241D87F18A5A93DF74E90FF94F4EB907F271A36B295B851F971DA5418F4915E2C1A23D6E";
            String cipherText =  SM2EncDecUtils.encrypt(Util.hexToByte(pubk), sourceData);
            sendData(cipherText);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean isSuccess(){
        try {
            //Socket socket = new Socket(ServerProp.ip, ServerProp.port);
            InputStream in = socket.getInputStream();
            byte[] buffer = new byte[1024];
            StringBuilder s = new StringBuilder();
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                s.append(new String(buffer, 0, len, "UTF-8"));
                System.out.println("接收到服务端的数据为：" + s.toString());
                return "OK".equals(s.toString());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
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
