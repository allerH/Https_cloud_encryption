package com.htj.cloudEnpt;

import cn.xjfme.encrypt.utils.sm4.SM4Utils;

public class Singleton {
    private static volatile SM4Utils sm4Utils = null;

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
}
