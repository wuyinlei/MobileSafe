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
import android.text.format.Formatter;
import android.text.style.ForegroundColorSpan;

import com.example.mobilesafe.activity.TaskManagerActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * 服务状态工具类
 */
public class SystemInfoUtils {

    /**
     * @param context     当前上下文
     * @param serviceName 服务的名字
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

    /**
     * 获取到系统的进程的总个数
     */
    public static int getProcessCount(Context context) {

        //得到进程的管理者(知道安装了多少app，可以获取进程数)
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);

        //获取当前手机上面的所有运行的进程
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        return runningAppProcesses.size();
    }

    /**
     * 获取到系统可用的内存
     * @param context
     * @return
     */
    public static String getAvailMem(Context context) {

        //得到进程的管理者(知道安装了多少app，可以获取进程数)
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);


        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        //获取到没存的基本信息
        activityManager.getMemoryInfo(memoryInfo);

        //获取到剩余内存
        String availMem = Formatter.formatFileSize(context, memoryInfo.availMem);

        return availMem;
    }

    /**
     * 获取到系统中总内存的信息
     * @param context
     * @return
     */
    public static long getTotalMem(Context context){
        /**
         * 这个是api 16及以上的使用的
         *
         * "/proc/meminfo"   这个是文件中存储的，是用下面的这个获取到的内存
         * 就不会有什么API版本不支持的问题
         *
         * MemTotal:    2541251KB
         *
         * 也就是说不能再API 16以下的手机上运用
         */


        try {
            //  /proc/meminfo  配置文件的路径
            FileInputStream fis = new FileInputStream(new File("/proc/meminfo"));

            //每次读取一行
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String readLine = reader.readLine();
            StringBuffer sb = new StringBuffer();
            for (char c : readLine.toCharArray()) {
                //装换成字符数组
                if (c >= '0' && c <= '9') {
                    sb.append(c);
                }
            }

            return Long.parseLong(sb.toString()) * 1024;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


}
