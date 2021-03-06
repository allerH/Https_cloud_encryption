package com.htj.cloudEnpt;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ServerProp {

    static String ip = "127.0.0.1";
    static  int port = 0;

    public static void initIpAndPort(String path) {
        Properties prop = new Properties();
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(path));
            prop.load(in);
            ip = prop.getProperty("ip");
            port = Integer.parseInt(prop.getProperty("port"));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
