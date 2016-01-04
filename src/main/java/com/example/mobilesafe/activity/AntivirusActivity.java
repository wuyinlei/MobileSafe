package com.example.mobilesafe.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.mobilesafe.R;
import com.example.mobilesafe.bean.APPinfo;
import com.example.mobilesafe.engine.AppInfos;
import com.example.mobilesafe.engine.TaskInfoParser;
import com.example.mobilesafe.utils.MD5Utils;

import java.util.List;

/**
 * 杀毒activity
 */
public class AntivirusActivity extends AppCompatActivity {

    private ImageView ivScanning;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antivirus);

        initialize();

        initData();
    }



    /**
     * 初始化布局控件
     */
    private void initialize() {
        ivScanning = (ImageView) findViewById(R.id.iv_scanning);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        initAnimation();

    }

    /**
     * 初始化动画
     */
    private void initAnimation() {
        /**
         * 第一个参数表示开始的角度
         * 第二个参数表示结束的角度
         * 第三个参数表示参照自己
         * 初始化旋转动画
         */
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        //设置时间
        rotateAnimation.setDuration(5000);
        //rotateAnimation.setRepeatMode(Animation.INFINITE);
        //设置动画无限循环
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        //开始动画
        ivScanning.startAnimation(rotateAnimation);
    }

    /**
     * 初始化数据
     */
    private void initData() {


        new Thread(){
            @Override
            public void run() {
                super.run();
                /**
                 * 获取到手机上的所有的安装的软件
                 *//*
                List<APPinfo> appInfos = AppInfos.getAppInfos(AntivirusActivity.this);

                //迭代所有手机上的所有软件
                for (APPinfo appInfo:appInfos) {
                    String apkName = appInfo.getApkName();
                }*/

                PackageManager packageManager = getPackageManager();

                List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);

                for (PackageInfo packageInfo:installedPackages) {
                    //得到应用程序的名字
                    String appName =  packageInfo.applicationInfo.loadLabel(packageManager).toString();

                    //首先需要获取到每个程序所在的位置
                    String sourceDir = packageInfo.applicationInfo.sourceDir;

                    //得到文件的MD5值
                    String md5 = MD5Utils.getFileMd5(sourceDir);

                    //判断当前文件的MD5值是否在病毒数据库中

                }


            }
        }.start();



    }

}
