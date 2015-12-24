package com.example.mobilesafe.activity;

/**
 * Created by 若兰 on 2015/12/24.
 * 一个懂得了编程乐趣的小白，希望自己
 * 能够在这个道路上走的很远，也希望自己学习到的
 * 知识可以帮助更多的人,分享就是学习的一种乐趣
 * QQ:1069584784
 * csdn:http://blog.csdn.net/wuyinlei
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.mobilesafe.R;

/**
 * 手机防盗
 */

public class LostFoubdActivity extends AppCompatActivity {

    private SharedPreferences mPres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPres = getSharedPreferences("config", MODE_PRIVATE);
        boolean config = mPres.getBoolean("configed", false);
        if (config) {
            setContentView(R.layout.activity_lost_found);
        } else {
            //跳转设置向导页
            startActivity(new Intent(LostFoubdActivity.this,Setup1Activity.class));
            finish();
        }
    }

    /**
     * 重新进入向导
     * @param view
     */
    public void reEnter(View view){
        startActivity(new Intent(LostFoubdActivity.this,Setup1Activity.class));
        finish();
    }
}
