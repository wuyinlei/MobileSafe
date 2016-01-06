package com.example.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.example.mobilesafe.activity.EnterPwdActivity;
import com.example.mobilesafe.db.AppLockDao;

import java.util.List;

public class WatchDogService extends Service {

    private List<ActivityManager.RunningTaskInfo> runningTasks;
    private ActivityManager.RunningTaskInfo taskInfo;
    private AppLockDao dao;
    private List<String> appLockInfos;

    public WatchDogService() {
    }

    ActivityManager activityManager;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private WatchDogReceiver receiver;
    @Override
    public void onCreate() {
        super.onCreate();
        //1、首先需要获取到当前任务栈
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        dao = new AppLockDao(this);

        //2、取任务栈最上面的任务

        appLockInfos = dao.findAll();
        receiver = new WatchDogReceiver();

        IntentFilter filter = new IntentFilter();
        //停止保护
        filter.addAction("com.example.mobilesafe.service.stopprotect");

        //注册一个锁屏的广播
        /**
         * 当屏幕锁住的时候。狗就休息
         * 屏幕解锁的时候。让狗活过来
         */
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        filter.addAction(Intent.ACTION_SCREEN_ON);


        registerReceiver(receiver, filter);





        startWatchDog();

    }

    //临时停止保护的包名
    private String tempStopProtectPackageName;

    private class WatchDogReceiver extends BroadcastReceiver {



        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals("com.example.mobilesafe.service.stopprotect")){
                //获取到停止保护的对象

                tempStopProtectPackageName = intent.getStringExtra("packageName");
            }else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                tempStopProtectPackageName = null;
                // 让狗休息
                flag = false;
            }else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                //让狗继续干活
                if(flag == false){
                    startWatchDog();
                }
            }


        }

    }

    //标记当前的看门狗是否停下来
    private boolean flag = false;

    private void startWatchDog() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                //一直在后台运行，为了避免造成主程序阻塞，开启线程

                flag = true;

                //死循环，一直在看门
                while (flag) {
                    //由于这个狗一直在后台运行。为了避免程序阻塞。
                    //获取到当前正在运行的任务栈
                    List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
                    //获取到最上面的进程
                    ActivityManager.RunningTaskInfo taskInfo = tasks.get(0);
                    //获取到最顶端应用程序的包名
                    String packageName = taskInfo.topActivity.getPackageName();



                    //Log.d("WatchDogService", packageName);
                    if (dao.find(packageName)) {
                       // Log.d("WatchDogService", "找到额");
                        //如果找到了，就在程序锁的数据库中
                        Intent intent = new Intent(WatchDogService.this, EnterPwdActivity.class);

                        /**
                         * 如果是在服务里面跳转到activity里面
                         *
                         * 一定要设置flag  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                         */
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        //没有找到，就没有在程序锁的数据库中
                       // Log.d("WatchDogService", "没有找到");
                    }
                }

            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = false;
        unregisterReceiver(receiver);
    }
}
