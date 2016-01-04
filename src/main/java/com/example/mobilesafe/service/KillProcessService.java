package com.example.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class KillProcessService extends Service {

    private LockScreenReceiver receiver;

    public KillProcessService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private class LockScreenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //得到进程管理器
            ActivityManager manager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
            //获取到手机上面所有正在进行的进程
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = manager.getRunningAppProcesses();

            for (ActivityManager.RunningAppProcessInfo runningInfo : runningAppProcesses) {
                manager.killBackgroundProcesses(runningInfo.processName);
            }

        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //锁屏的广播
        receiver = new LockScreenReceiver();

        //锁屏的过滤器
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);

        //注册一个锁屏的广播
        registerReceiver(receiver, intentFilter);

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //在这里写我们的业务逻辑

            }
        };

        /**
         * 第一个参数  表示用哪个类进行调度
         *
         * 第二个表示多长时间
         *
         * 第三个表示循环
         */
        timer.schedule(timerTask, 1000, 1000);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        //这个要反注册掉，要不然在程序中会出错，虽然这个错误显示不了
        unregisterReceiver(receiver);
        //手动回收
        receiver = null;
    }
}
