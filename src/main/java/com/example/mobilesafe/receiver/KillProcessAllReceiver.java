package com.example.mobilesafe.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.List;

/**
 * Created by 若兰 on 2016/1/4.
 * 一个懂得了编程乐趣的小白，希望自己
 * 能够在这个道路上走的很远，也希望自己学习到的
 * 知识可以帮助更多的人,分享就是学习的一种乐趣
 * QQ:1069584784
 * csdn:http://blog.csdn.net/wuyinlei
 */

/**
 * 杀死所有的进程的服务
 */
public class KillProcessAllReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);

        //得到手机上面所有的正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();

        /**
         * 迭代一下清理所有的进程
         */
        for (ActivityManager.RunningAppProcessInfo runningProcessInfo:runningAppProcesses) {
            activityManager.killBackgroundProcesses(runningProcessInfo.processName);
        }

        Toast.makeText(context, "清理完毕", Toast.LENGTH_SHORT).show();
    }
}
