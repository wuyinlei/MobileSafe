package com.example.mobilesafe.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.mobilesafe.R;
import com.example.mobilesafe.view.SettingItemView;

/**
 * Created by 若兰 on 2015/12/23.
 */
public class SettingActivity extends AppCompatActivity {

    private TextView tvtitle;
    private TextView tvdesc;
    private SettingItemView siv_update;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mPrefs = getSharedPreferences("config", MODE_PRIVATE);
        initialize();
    }

    /**
     * 初始化控件
     */
    private void initialize() {
        tvtitle = (TextView) findViewById(R.id.tv_title);
        tvdesc = (TextView) findViewById(R.id.tv_desc);
        siv_update = (SettingItemView) findViewById(R.id.siv_update);
        siv_update.setTitle("自动更新设置");
        final boolean autoUpdate = mPrefs.getBoolean("auto_update", true);
        if (autoUpdate) {
            siv_update.setDesc("自动更新已开启");
            siv_update.setChecked(true);
        }else {
            siv_update.setDesc("自动更新已关闭");
            siv_update.setChecked(false);
        }
        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断当前的勾选状态
                if (siv_update.isChecked()) {
                    //设置不勾选
                    siv_update.setChecked(false);
                    siv_update.setDesc("自动更新已关闭");

                    //更新SP
                    mPrefs.edit().putBoolean("auto_update", false).commit();
                } else {
                    siv_update.setChecked(true);
                    siv_update.setDesc("自动更新已经开启");
                    mPrefs.edit().putBoolean("auto_update", true).commit();
                }
            }
        });
    }
}
