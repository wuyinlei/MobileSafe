package com.example.mobilesafe.utils;

import android.speech.RecognitionService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by 若兰 on 2015/12/24.
 * 一个懂得了编程乐趣的小白，希望自己
 * 能够在这个道路上走的很远，也希望自己学习到的
 * 知识可以帮助更多的人,分享就是学习的一种乐趣
 * QQ:1069584784
 * csdn:http://blog.csdn.net/wuyinlei
 */

public class MD5Utils {


    /**
     * MD5加密
     *
     * @param password
     * @return
     */
    public static String encode(String password) {
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");  //获取MD5算法对象
            byte[] diget = instance.digest(password.getBytes());   //对字符加密，返回字节数组
            StringBuffer sb = new StringBuffer();
            for (byte b : diget) {
                int i = b & 0xff;   //获取字节的低8位有效值
                String hexString = Integer.toHexString(i);    //将数转为16进制
                System.out.println(hexString);
                if (hexString.length() < 2) {
                    hexString = "0" + hexString;
                }
                sb.append(hexString);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取文件的MD5值
     *
     * @param sourceDir
     * @return
     */
    public static String getFileMd5(String sourceDir) {

        File file;
        FileInputStream fis;
        file = new File(sourceDir);
        StringBuffer sb = new StringBuffer();

        try {
            fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len = -1;

            //获取到MD5数字摘要
            MessageDigest messageDigest = MessageDigest.getInstance("md5");
            while ((len = fis.read(buffer)) != -1) {
                messageDigest.update(buffer, 0, len);
            }
            byte[] result = messageDigest.digest();
            for (byte b : result) {
                int i = b & 0xff;   //获取字节的低8位有效值
                String hexString = Integer.toHexString(i);    //将数转为16进制
                System.out.println(hexString);
                if (hexString.length() < 2) {
                    hexString = "0" + hexString;
                }
                sb.append(hexString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
