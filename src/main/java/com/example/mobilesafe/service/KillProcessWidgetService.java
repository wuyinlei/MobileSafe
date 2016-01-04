package com.example.mobilesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.mobilesafe.R;
import com.example.mobilesafe.receiver.MyWidgerProvider;
import com.example.mobilesafe.utils.SystemInfoUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 清理桌面小控件的服务
 */
public class KillProcessWidgetService extends Service {

    private AppWidgetManager appWidgetManager;

    public KillProcessWidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //桌面小控件管理者
        appWidgetManager = AppWidgetManager.getInstance(KillProcessWidgetService.this);
        //每隔5秒钟更新一下桌面
        Timer timer = new Timer();

        //初始化一个定时任务
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                //这个是把当前的布局文件添加进来
                //初始化一个远程的View
                //把自己的view跑到了桌面上(别人的view上)
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);

                //通过findViewById找不到
                /**
                 * 通过设置文本控件的内容设置
                 *
                 * 设置当前文本里面一共有多少个进程
                 */
                int processCount = SystemInfoUtils.getProcessCount(getApplicationContext());
                views.setTextViewText(R.id.process_count, "正在运行的进程 ：" + processCount + "个");

                //Log.d("KillProcessWidgetServic", SystemInfoUtils.getAvailMem(getApplicationContext()));
                views.setTextViewText(R.id.process_memory, "可用内存" + SystemInfoUtils.getAvailMem(getApplicationContext()));


                Intent intent = new Intent();

                //发送一个隐式的意图，让系统知道是谁去处理事件
                intent.setAction("com.example.mobilesafe");

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                views.setOnClickPendingIntent(R.id.btn_clear,pendingIntent);


                /**
                 * 第一个参数表示上下文
                 *
                 * 第二个参数表示当前哪一个广播进行去处理当前的桌面小控件
                 */

                ComponentName provider = new ComponentName(getApplicationContext(), MyWidgerProvider.class);

                //更新桌面
                appWidgetManager.updateAppWidget(provider, views);

            }
        };

        /**
         * schedule  有三个参数
         * 第一个是TimerTask   一个定时任务
         * 第二个是delay    多长时间的延迟时间    amount of time in milliseconds before first execution.
         * 第三个是period   amount of time in milliseconds between subsequent executions
         */
        //从 0 开始，每隔五秒钟调用一次
        timer.schedule(timerTask, 0, 5000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
