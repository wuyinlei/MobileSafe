package com.example.mobilesafe.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by 若兰 on 2016/1/3.
 * 一个懂得了编程乐趣的小白，希望自己
 * 能够在这个道路上走的很远，也希望自己学习到的
 * 知识可以帮助更多的人,分享就是学习的一种乐趣
 * QQ:1069584784
 * csdn:http://blog.csdn.net/wuyinlei
 */

public class UIUtils {

    public static void showToast(final Activity context,final String msg){
        if ("main".equals(Thread.currentThread().getName())){
            Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
        } else {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

}
