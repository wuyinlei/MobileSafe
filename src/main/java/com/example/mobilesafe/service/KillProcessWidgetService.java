package com.example.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 清理桌面小控件的服务
 */
public class KillProcessWidgetService extends Service {
    public KillProcessWidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //每隔5秒钟更新一下桌面
        Timer timer = new Timer();

        //初始化一个定时任务
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

            }
        };

        /**
         * schedule  有三个参数
         * 第一个是TimerTask   一个定时任务
         * 第二个是delay    多长时间的延迟时间    amount of time in milliseconds before first execution.
         * 第三个是period   amount of time in milliseconds between subsequent executions
         */
        //从 0 开始，每隔五秒钟调用一次
        timer.schedule(timerTask,0,5000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
