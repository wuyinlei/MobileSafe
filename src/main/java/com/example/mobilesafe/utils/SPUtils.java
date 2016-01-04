package com.example.mobilesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 若兰 on 2016/1/4.
 * 一个懂得了编程乐趣的小白，希望自己
 * 能够在这个道路上走的很远，也希望自己学习到的
 * 知识可以帮助更多的人,分享就是学习的一种乐趣
 * QQ:1069584784
 * csdn:http://blog.csdn.net/wuyinlei
 */

public class SPUtils {

    public static final String SP_NAME = "config";

    public static void saveBoolean(Context context, String key, boolean value) {
        SharedPreferences mPres = context.getSharedPreferences(SP_NAME, 0);
        mPres.edit().putBoolean(key, value).commit();
    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {
        SharedPreferences mPres = context.getSharedPreferences(key, 0);
        return mPres.getBoolean(key, defValue);
    }
}
