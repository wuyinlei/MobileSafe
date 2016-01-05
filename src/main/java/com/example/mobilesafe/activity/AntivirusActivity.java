package com.example.mobilesafe.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.EdgeEffect;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.mobilesafe.R;
import com.example.mobilesafe.bean.APPinfo;
import com.example.mobilesafe.db.AntivirusDao;
import com.example.mobilesafe.engine.AppInfos;
import com.example.mobilesafe.engine.TaskInfoParser;
import com.example.mobilesafe.utils.MD5Utils;

import java.util.List;

/**
 * 杀毒activity
 */
public class AntivirusActivity extends AppCompatActivity {

    /**
     * 扫描前
     */
    private static final int BEGIN = 0;

    /**
     * 扫描中
     */
    private static final int SCANING = 1;

    /**
     * 扫描结束
     */
    private static final int FINISH = 2;


    private ImageView ivScanning;
    private ProgressBar progressBar;
    private TextView tv_init_virus;
    private LinearLayout ll_content;
    private ScrollView scrollView;


    private ScanInfo scanInfo;
    private Message message;
    private int size;

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
        tv_init_virus = (TextView) findViewById(R.id.tv_init_virus);
        ll_content = (LinearLayout) findViewById(R.id.ll_content);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
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
     * 接收消息的handler
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BEGIN:
                    tv_init_virus.setText("初始化16核杀毒引擎");
                    break;
                case SCANING:
                    tv_init_virus.setText("正在查杀病毒");


                    TextView child = new TextView(AntivirusActivity.this);
                    ScanInfo scanInfo = (ScanInfo) msg.obj;

                    child.setTextSize(24);
                    if (!scanInfo.desc) {
                        //如果为false为没有病毒
                        child.setText(scanInfo.appName + "扫描安全");
                        child.setTextColor(Color.GRAY);
                       // Log.d("AntivirusActivity", "scanInfo.desc:" + scanInfo.desc);
                    } else {
                        child.setText(scanInfo.appName + "有病毒");
                        child.setTextColor(Color.RED);
                    }
                    ll_content.addView(child);

                    //自动滚动
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.fullScroll(scrollView.FOCUS_DOWN);
                        }
                    });


                    break;
                case FINISH:
                    tv_init_virus.setText("查杀完成");
                    ivScanning.clearAnimation();//停止动画
                    break;
            }
        }
    };

    /**
     * 初始化数据
     */
    private void initData() {


        new Thread() {
            @Override
            public void run() {
                super.run();


                //杀毒刚开始的时候的消息
                message = Message.obtain();
                message.what = BEGIN;

                /**
                 * 获取到手机上的所有的安装的软件
                 *//*
                List<APPinfo> appInfos = AppInfos.getAppInfos(AntivirusActivity.this);

                //迭代所有手机上的所有软件
                for (APPinfo appInfo:appInfos) {
                    String apkName = appInfo.getApkName();
                }*/

                scanInfo = new ScanInfo();

                PackageManager packageManager = getPackageManager();

                List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);

                //获得总共有多少个应用程序
                size = installedPackages.size();

                int process = 0;

                //设置进度条的最大值
                progressBar.setMax(size);

                for (PackageInfo packageInfo : installedPackages) {
                    //得到应用程序的名字
                    String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();

                    //得到应用的名字
                    scanInfo.appName = appName;

                    //得到应用的包名
                    scanInfo.packageName = packageInfo.applicationInfo.packageName;

                    //首先需要获取到每个程序所在的位置
                    String sourceDir = packageInfo.applicationInfo.sourceDir;

                    //得到文件的MD5值
                    String md5 = MD5Utils.getFileMd5(sourceDir);

                   // Log.d("AntivirusActivity", md5 + "      "+ appName);
                    //判断当前文件的MD5值是否在病毒数据库中
                    String desc = AntivirusDao.checkFileVirus(md5);


                    if (desc == null) {
                        //如果当前的描述信息等于null，代表没有病毒
                        scanInfo.desc = false;
                    } else {
                        scanInfo.desc = true;
                    }

                    process++;

                    SystemClock.sleep(100);
                    progressBar.setProgress(process);

                    //杀毒进行的时候的消息
                    message = Message.obtain();
                    message.what = SCANING;
                    message.obj = scanInfo;

                    handler.sendMessage(message);

                }

                //杀毒完成后的消息
                message = Message.obtain();
                message.what = FINISH;
                handler.sendMessage(message);

            }
        }.start();


    }

    static class ScanInfo {
        boolean desc;
        String appName;
        String packageName;
    }

}
