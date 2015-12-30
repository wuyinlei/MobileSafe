package com.example.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.example.mobilesafe.bean.APPinfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 若兰 on 2015/12/29.
 * 一个懂得了编程乐趣的小白，希望自己
 * 能够在这个道路上走的很远，也希望自己学习到的
 * 知识可以帮助更多的人,分享就是学习的一种乐趣
 * QQ:1069584784
 * csdn:http://blog.csdn.net/wuyinlei
 */

public class AppInfos {

    private static PackageManager packageManager;
    private static List<APPinfo> apPinfos = new ArrayList<>();

    //获取到所有的应用信息
    public static List<APPinfo> getAppInfos(Context context) {

        /**
         * 获取到包的管理者
         */
        packageManager = context.getPackageManager();
        //获取到整个安装到手机上的安装包
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);

        //迭代集合
        for (PackageInfo installedPackage : installedPackages) {

            //先创建一个Appinfo对象
            APPinfo apPinfo = new APPinfo();

            //获取到应用程序的图标
            Drawable icon = installedPackage.applicationInfo.loadIcon(packageManager);
            apPinfo.setIcon(icon);

            //获取到应用程序的名字
            String apkName = (String) installedPackage.applicationInfo.loadLabel(packageManager);
            apPinfo.setApkName(apkName);

            //获取到应用程序的包名
            String packageName = installedPackage.applicationInfo.packageName;
            apPinfo.setApkPageName(packageName);

            //获取到apk资源的路径
            String sourceDir = installedPackage.applicationInfo.sourceDir;
            File file = new File(sourceDir);
            //apk的长度   size
            long length = file.length();
            apPinfo.setApkSize(length);

            //第三方应用   /data/data     系统应用   /System/app
            //获取到安装应用程序的标记
            int flags = installedPackage.applicationInfo.flags;

            /**
             * 通过flags的一些标记来判断是属于系统应用的还是第三方应用的
             */

            if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                //表示系统app
                apPinfo.setUserApp(false);
            } else {
                //表示用户app
                apPinfo.setUserApp(true);
            }

            /**
             * 通过flags的一些标记来判断是放置在SD卡的还是手机内存中的
             */
            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                //便是在SD卡
                apPinfo.setIsRom(false);
            } else {
                //表示手机内存
                apPinfo.setIsRom(true);
            }


            apPinfos.add(apPinfo);


            //Log.d("AppInfos",  apkName + packageName + length);

        }

        return apPinfos;
    }
}
