package com.example.mobilesafe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.mobilesafe.R;
import com.example.mobilesafe.service.AddressService;
import com.example.mobilesafe.utils.ServiceStateUtils;
import com.example.mobilesafe.view.SettingItemView;

/**
 * Created by 若兰 on 2015/12/23.
 */
public class SettingActivity extends AppCompatActivity {

    private TextView tvtitle;
    private TextView tvdesc;
    private SettingItemView siv_update,siv_address;
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
        siv_address = (SettingItemView) findViewById(R.id.siv_address);
        initAddressService();
        initUpdate();
    }

    /**
     * 初始化是否自动更新，自动更新逻辑的处理
     */
    private void initUpdate() {
        final boolean autoUpdate = mPrefs.getBoolean("auto_update", true);
        if (autoUpdate) {
            siv_update.setChecked(true);
        }else {
            siv_update.setChecked(false);
        }
        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断当前的勾选状态
                if (siv_update.isChecked()) {
                    //设置不勾选
                    siv_update.setChecked(false);
                    //更新SP
                    mPrefs.edit().putBoolean("auto_update", false).commit();
                } else {
                    siv_update.setChecked(true);
                    mPrefs.edit().putBoolean("auto_update", true).commit();
                }
            }
        });
    }

    /**
     * 初始化电话号码归属地的显示的服务
     */
    private void initAddressService() {

        //根据归属地服务是否运行来更新选择框
        boolean serviceRunning = ServiceStateUtils.isServiceRunning(this, "com.example.mobilesafe.service");

        //判断该服务是否运行，如果没有运行，勾选框就会不勾选
        if (serviceRunning){
            siv_address.setChecked(true);
        } else {
            siv_address.setChecked(false);
        }
        siv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (siv_address.isChecked()){
                    siv_address.setChecked(false);
                    //停止归属地服务
                    stopService(new Intent(SettingActivity.this,AddressService.class));
                } else {
                    siv_address.setChecked(true);
                    //开启归属地服务
                    startService(new Intent(SettingActivity.this, AddressService.class));
                }
            }
        });
    }
}
