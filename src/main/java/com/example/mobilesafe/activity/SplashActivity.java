package com.example.mobilesafe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mobilesafe.R;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int CODE_CENTER_HOME = 0;
    private SharedPreferences mPres;
    private TextView tv_version;
    private Button btn_start;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CODE_CENTER_HOME:
                    enterHome();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mPres = getSharedPreferences("config",MODE_PRIVATE);
        //判断是否自动更新
        boolean autoUpdate = mPres.getBoolean("auto_update",true);
        if (autoUpdate){
            checkVersion();
        } else {
            handler.sendEmptyMessageAtTime(CODE_CENTER_HOME,2000);
        }
        initControl();
    }

    private void checkVersion() {

    }

    private void enterHome(){
        startActivity(new Intent(SplashActivity.this,HomeActivity.class));
        finish();
    }


    /**
     * 初始化控件
     */
    private void initControl() {
        btn_start= (Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(this);
        tv_version = (TextView) findViewById(R.id.tv_version);
        tv_version.setText("版本号:" + getVersionName());
    }

    /**
     * 获取版本号
     *
     * @return
     */
    private String getVersionName() {
        String versionName="";
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
}
