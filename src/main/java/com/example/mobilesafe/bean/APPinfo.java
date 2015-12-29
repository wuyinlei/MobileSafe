package com.example.mobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by 若兰 on 2015/12/29.
 * 一个懂得了编程乐趣的小白，希望自己
 * 能够在这个道路上走的很远，也希望自己学习到的
 * 知识可以帮助更多的人,分享就是学习的一种乐趣
 * QQ:1069584784
 * csdn:http://blog.csdn.net/wuyinlei
 */

public class APPinfo {

    /**
     * 表示只要所有可以放到Drawable目录下的都可以表示
     *
     * 如果是Bitmap的话只能存放图片
     */
    private Drawable icon;

    /**
     * app的名字
     */
    private String apkName;

    /**
     * 程序的大小
     */
    private long apkSize;

    /**
     * 表示到底是用户app还是系统app
     *
     * true  -- >用户app
     * false -- >系统app
     */
    private boolean userApp;

    /**
     * 放置的位置
     * true  -->Rom里面
     * false -->SD卡里面
     */
    private boolean isRom;

    /**
     * 程序的包名
     */
    private String apkPageName;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    public long getApkSize() {
        return apkSize;
    }

    public void setApkSize(long apkSize) {
        this.apkSize = apkSize;
    }

    public boolean isRom() {
        return isRom;
    }

    public void setIsRom(boolean isRom) {
        this.isRom = isRom;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }

    public String getApkPageName() {
        return apkPageName;
    }

    public void setApkPageName(String apkPageName) {
        this.apkPageName = apkPageName;
    }

    @Override
    public String toString() {
        return "APPinfo{" +
                "icon=" + icon +
                ", apkName='" + apkName + '\'' +
                ", apkSize=" + apkSize +
                ", userApp=" + userApp +
                ", isRom=" + isRom +
                ", apkPageName='" + apkPageName + '\'' +
                '}';
    }
}
