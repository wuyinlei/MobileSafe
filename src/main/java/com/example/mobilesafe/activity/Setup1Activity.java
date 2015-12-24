package com.example.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.mobilesafe.R;

/**
 * Created by 若兰 on 2015/12/24.
 * 一个懂得了编程乐趣的小白，希望自己
 * 能够在这个道路上走的很远，也希望自己学习到的
 * 知识可以帮助更多的人,分享就是学习的一种乐趣
 * QQ:1069584784
 * csdn:http://blog.csdn.net/wuyinlei
 */

/**
 * 第一个向导页
 */
public class Setup1Activity extends BaseSetupActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_1);
    }

    @Override
    public void showPreviousPage() {

    }

    @Override
    public void showNextPage() {
        startActivity(new Intent(Setup1Activity.this, Setup2Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_out, R.anim.tran_in);
    }
}
