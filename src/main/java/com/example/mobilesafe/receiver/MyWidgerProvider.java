package com.example.mobilesafe.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.mobilesafe.service.KillProcessWidgetService;

/**
 * Created by 若兰 on 2016/1/4.
 * 一个懂得了编程乐趣的小白，希望自己
 * 能够在这个道路上走的很远，也希望自己学习到的
 * 知识可以帮助更多的人,分享就是学习的一种乐趣
 * QQ:1069584784
 * csdn:http://blog.csdn.net/wuyinlei
 */

public class MyWidgerProvider extends AppWidgetProvider {

    /**
     * 第一次创建的时候才会去创建
     * @param context
     * @param intent
     *//*
    @Override
    public void onReceive(Context context, Intent intent) {

    }
*/
   /* *//**
     * 每次有新的桌面小控件生成的时候都会去创建
     * @param context
     * @param appWidgetManager
     * @param appWidgetIds
     *//*
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
*/
   /* *//**
     * 每次删除桌面小控件的时候都会去掉用
     * @param context
     * @param appWidgetIds
     *//*
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }
*/
    /**
     * 第一次创建的时候才会调用此生命周期的方法
     *
     * 当前的广播的生命周期只有10秒钟
     *
     * 不能做耗时的操作    所以要调到其他的页面操作
     * @param context
     */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        //开启服务
        Intent intent = new Intent(context, KillProcessWidgetService.class);
        context.startService(intent);
    }

    /**
     * 当桌面上所有的桌面小控件都删除的时候调用   相当于onDestroy方法
     * @param context
     */
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

        //关闭服务
        Intent intent = new Intent(context, KillProcessWidgetService.class);
        context.stopService(intent);
    }
}
