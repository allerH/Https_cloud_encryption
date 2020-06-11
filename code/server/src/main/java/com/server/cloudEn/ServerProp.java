package com.server.cloudEn;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ServerProp {
    static int port = 7878;

    public static void initPort(String path){
        try {
            Properties prop = new Properties();
            InputStream in = new BufferedInputStream(new FileInputStream(path));
            prop.load(in);
            port = Integer.parseInt(prop.getProperty("port"));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
