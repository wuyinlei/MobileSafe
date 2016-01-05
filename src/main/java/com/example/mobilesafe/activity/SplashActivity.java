package com.example.mobilesafe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mobilesafe.R;
import com.example.mobilesafe.bean.Virus;
import com.example.mobilesafe.db.AntivirusDao;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 闪屏页面，也就是刚下载程序后的初始化界面
 */
public class SplashActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout rlRoot;
    private static final int CODE_CENTER_HOME = 0;
    private SharedPreferences mPres;
    private TextView tv_version;
    private Button btn_start;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_CENTER_HOME:
                    enterHome();
                    break;
            }
        }
    };
    private AntivirusDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mPres = getSharedPreferences("config", MODE_PRIVATE);
        //copy地址数据库
        copyDB("address.db");

        //copy病毒数据库
        copyDB("antivirus.db");

        createShortcut();

        updateVirus();

        //判断是否自动更新
        boolean autoUpdate = mPres.getBoolean("auto_update", true);
        if (autoUpdate) {
            checkVersion();
        } else {
            handler.sendEmptyMessageAtTime(CODE_CENTER_HOME, 2000);
        }
        initControl();
    }

    /**
     * 更新病毒数据库
     */
    private void updateVirus() {

        //进行联网，从服务器获取到最新的md5特征码

        HttpUtils httpUtils = new HttpUtils();

        String url = "http://113.229.155.154:8080/virus.json";
        httpUtils.send(HttpMethod.GET, url, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                Log.d("SplashActivity", arg0.result);
//				﻿{"md5":"51dc6ba54cbfbcff299eb72e79e03668","desc":"蝗虫病毒赶快卸载"}

                try {
                    JSONObject jsonObject = new JSONObject(arg0.result);

                    dao = new AntivirusDao();

                    //google的json解析
                    Gson gson = new Gson();

                    /**
                     * {"md5":"bad92c883efe5e87937baf206e1faa12","desc":"蝗虫病毒，赶快卸载"}
                     *
                     *  在这里我们只需要定义好我们得额javabean对象就行，key和服务器的key要一一对应
                     *
                     *  比如上面的我们的那个服务器的key是md5  和  desc
                     *  那么我们的javabean中的字段名也要是md5 和 desc  大小写也要是一样的，这样才能
                     *  继续往下工作，要不然解析不出来
                     */


                    Virus virus = gson.fromJson(arg0.result,Virus.class);
                /*
                    String md5 = jsonObject.getString("md5");
                    String desc = jsonObject.getString("desc");*/
                    //dao.addVirus(virus.md5,virus.desc);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        });



    }

    /**
     * 创建快捷方式
     */
    private void createShortcut() {

        Intent intent = new Intent();
        /**
         * 1 干什么事情
         * 2 叫什么名字
         * 3 长什么样子
         */
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

        //长什么样子
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

        //如果为true，就是默认的创建重复的快捷方式 ，如果传入false，就是不重复创建快捷方式
        intent.putExtra("duplicate",false);
        
        //叫什么名字
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机卫士");

        //干什么事情
        /**
         *这个地方不能使用显示的意图
         *必须使用隐式的意图，因为这里我们传入了this--->指明了当前的对象、
         *但是当我们创建好图标后，我们在去的点击图标进入到我们想要的界面的时候
         *this就不是指示的当前对象了
         */
        Intent shortcut_intent = new Intent();

        shortcut_intent.setAction("aaa");
        shortcut_intent.addCategory("android.intent.category.DEFAULT");

        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcut_intent);


        sendBroadcast(intent);

    }

    private void checkVersion() {

    }

    private void enterHome() {
        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
        finish();
    }


    /**
     * 初始化控件
     */
    private void initControl() {
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(this);
        tv_version = (TextView) findViewById(R.id.tv_version);
        tv_version.setText("版本号:" + getVersionName());
        rlRoot = (RelativeLayout) findViewById(R.id.rlRoot);

        //渐变的动画效果
        AlphaAnimation anim = new AlphaAnimation(0.3f, 1f);
        anim.setDuration(2000);
        rlRoot.startAnimation(anim);
    }

    /**
     * 获取版本号
     *
     * @return
     */
    private String getVersionName() {
        String versionName = "";
        PackageManager packageManager = getPackageManager();
        try {
            //获取包的信息
            PackageInfo info = packageManager.getPackageInfo(getPackageName(), 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    @Override
    public void onClick(View v) {
        enterHome();
    }

    /**
     * copy数据库
     */
    private void copyDB(String dbName) {

        //获取文件路径
        File descFile = new File(getFilesDir(), dbName);
        FileOutputStream out = null;
        InputStream in = null;
        if (descFile.exists()) {
            Log.d("SplashActivity", descFile + "该数据库已经存在");
            return;
        }
        try {
            in = getAssets().open(dbName);  //要拷贝的目标地址
            out = new FileOutputStream(descFile);
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
