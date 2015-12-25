package com.example.mobilesafe.utils;

/**
 * Created by 若兰 on 2015/12/25.
 * 一个懂得了编程乐趣的小白，希望自己
 * 能够在这个道路上走的很远，也希望自己学习到的
 * 知识可以帮助更多的人,分享就是学习的一种乐趣
 * QQ:1069584784
 * csdn:http://blog.csdn.net/wuyinlei
 */

import android.app.ActivityManager;
import android.content.Context;
import android.text.style.ForegroundColorSpan;

import java.util.List;

/**
 * 服务状态工具类
 */
public class ServiceStateUtils {

    /**
     *
     * @param context   当前上下文
     * @param serviceName   服务的名字
     * @return
     */
    public static boolean isServiceRunning(Context context, String serviceName) {

        ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);

        //获取系统运行的所有的服务，最多返回100个
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(100);

        for (ActivityManager.RunningServiceInfo runningServiceInfo :
                runningServices) {
            //获取服务的名称
            String className = runningServiceInfo.service.getClassName();
            if (className.equals(serviceName)) {
                return true;
            }
        }
        return false;
    }
}
