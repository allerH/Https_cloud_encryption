package cn.xjfme.encrypt.utils.sm4;

/**
 * Created by $(USER) on $(DATE)
 */

import cn.xjfme.encrypt.utils.Util;
import com.sun.deploy.util.SyncAccess;
import org.apache.commons.codec.binary.Base64;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SM4Utils {
//	private String secretKey = "";
//    private String iv = "";
//    private boolean hexString = false;

    public String secretKey = "64EC7C763AB7BF64E2D75FF83A319918";
    public String iv = "31313131313131313131313131313131";
    public boolean hexString = true;

    public SM4Utils() {
    }


    public String encryptData_ECB(String plainText) {
        try {
            SM4_Context ctx = new SM4_Context();
            ctx.isPadding = true;
            ctx.mode = SM4.SM4_ENCRYPT;

            byte[] keyBytes;
            if (hexString) {
                keyBytes = Util.hexStringToBytes(secretKey);
            } else {
                //keyBytes = secretKey.getBytes();
                keyBytes = Util.hexStringToBytes(secretKey);
            }

            SM4 sm4 = new SM4();
            sm4.sm4_setkey_enc(ctx, keyBytes);
            byte[] encrypted = sm4.sm4_crypt_ecb(ctx, plainText.getBytes("UTF-8"));
            return Util.byteToHex(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String decryptData_ECB(String cipherText) {
        try {
            byte[] encrypted = Util.hexToByte(cipherText);
            cipherText=Base64.encodeBase64String(encrypted);;
            //cipherText = new BASE64Encoder().encode(encrypted);
            if (cipherText != null && cipherText.trim().length() > 0) {
                Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                Matcher m = p.matcher(cipherText);
                cipherText = m.replaceAll("");
            }

            SM4_Context ctx = new SM4_Context();
            ctx.isPadding = true;
            ctx.mode = SM4.SM4_DECRYPT;

            byte[] keyBytes;
            if (hexString) {
                keyBytes = Util.hexStringToBytes(secretKey);
            } else {
                keyBytes = secretKey.getBytes();
            }

            SM4 sm4 = new SM4();
            sm4.sm4_setkey_dec(ctx, keyBytes);
            byte[] decrypted = sm4.sm4_crypt_ecb(ctx, Base64.decodeBase64(cipherText));
            //byte[] decrypted = sm4.sm4_crypt_ecb(ctx, new BASE64Decoder().decodeBuffer(cipherText));
            return new String(decrypted, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String encryptData_CBC(String plainText) {
        try {
            SM4_Context ctx = new SM4_Context();
            ctx.isPadding = true;
            ctx.mode = SM4.SM4_ENCRYPT;

            byte[] keyBytes;
            byte[] ivBytes;
            if (hexString) {
                keyBytes = Util.hexStringToBytes(secretKey);
                ivBytes = Util.hexStringToBytes(iv);
            } else {
                keyBytes = secretKey.getBytes();
                ivBytes = iv.getBytes();
            }

            SM4 sm4 = new SM4();
            sm4.sm4_setkey_enc(ctx, keyBytes);
            byte[] encrypted = sm4.sm4_crypt_cbc(ctx, ivBytes, plainText.getBytes("UTF-8"));
            return Util.byteToHex(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String decryptData_CBC(String cipherText) {
        try {
            byte[] encrypted = Util.hexToByte(cipherText);
            cipherText=Base64.encodeBase64String(encrypted);;
            //cipherText = new BASE64Encoder().encode(encrypted);
            if (cipherText != null && cipherText.trim().length() > 0) {
                Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                Matcher m = p.matcher(cipherText);
                cipherText = m.replaceAll("");
            }
            SM4_Context ctx = new SM4_Context();
            ctx.isPadding = true;
            ctx.mode = SM4.SM4_DECRYPT;

            byte[] keyBytes;
            byte[] ivBytes;
            if (hexString) {
                keyBytes = Util.hexStringToBytes(secretKey);
                ivBytes = Util.hexStringToBytes(iv);
            } else {
                keyBytes = secretKey.getBytes();
                ivBytes = iv.getBytes();
            }

            SM4 sm4 = new SM4();
            sm4.sm4_setkey_dec(ctx, keyBytes);
            //byte[] decrypted = sm4.sm4_crypt_cbc(ctx, ivBytes, new BASE64Decoder().decodeBuffer(cipherText));
            byte[] decrypted = sm4.sm4_crypt_cbc(ctx, ivBytes, Base64.decodeBase64(cipherText));
            /*String text = new String(decrypted, "UTF-8");
            return text.substring(0,text.length()-1);*/
            return new String(decrypted, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void avgTime(int n){
        String plainText = "hello world";
        //System.out.println("plainText = " + plainText);
        String s = Util.byteToHex(plainText.getBytes());
        SM4Utils sm4 = new SM4Utils();
        sm4.secretKey = "64EC7C763AB7BF64E2D75FF83A319918";
        sm4.hexString = true;
        System.out.println("CBC模式加密");
        long encrptyAvgTime = 0L;
        long decrptyAvgTime = 0L;
        for (int i =0 ; i< n; i++){
            long start = System.nanoTime();
            String cipherText1 = sm4.encryptData_CBC(plainText);
            //System.out.println("加密后的密文: " + cipherText1);
            long end = System.nanoTime();
            encrptyAvgTime += (end - start)/ 1000000L;

            start = System.nanoTime();
            sm4.decryptData_CBC(cipherText1);
            end = System.nanoTime();
            decrptyAvgTime += (end - start)/1000000L;
        }
        System.out.println(n + "次加密的平均时间：" + (double) encrptyAvgTime/n + "ms");
        System.out.println(n + "次解密的平均时间：" + (double) decrptyAvgTime/n + "ms");
    }

    public static void main(String[] args) throws IOException {
        avgTime(100);
        String plainText = "hello world";
        System.out.println("plainText = " + plainText);
        String s = Util.byteToHex(plainText.getBytes());
        //System.out.println("原文" + s);
        SM4Utils sm4 = new SM4Utils();
        //sm4.secretKey = "JeF8U9wHFOMfs2Y8";
        sm4.secretKey = "64EC7C763AB7BF64E2D75FF83A319918";
        sm4.hexString = true;
        System.out.println("CBC模式加密");
        sm4.iv = "31313131313131313131313131313131";
        long start = System.nanoTime();
        String cipherText2 = sm4.encryptData_CBC(plainText);
        long end = System.nanoTime();
        System.out.println("加密后的密文: " + cipherText2);
        System.out.println("CBC模式加密花费的时间：" + (double)(end - start)/ 1000000L+ "ms");

        start = System.nanoTime();
        String plainText3 = sm4.decryptData_CBC(cipherText2);
        end = System.nanoTime();
        System.out.println("解密明文: " + plainText3);
        System.out.println("CBC模式解密花费的时间：" + (double)(end - start)/ 1000000L+ "ms");

        System.out.println("------------------------------------");
        System.out.println("ECB模式加密");
        start = System.nanoTime();
        String cipherText1 = sm4.encryptData_ECB(plainText);

        end = System.nanoTime();
        System.out.println("加密后的密文: " + cipherText1);
        System.out.println("ECB模式加密所花费的时间：" + (double)(end - start)/ 1000000L + "ms");

        start = System.nanoTime();
        String plaintext_ecb = sm4.decryptData_ECB(cipherText1);
        end = System.nanoTime();
        System.out.println("解密后的明文：" + plaintext_ecb);
        System.out.println("ECB模式解密花费的时间：" + (double)(end - start)/ 1000000L + "ms");



    }
}
