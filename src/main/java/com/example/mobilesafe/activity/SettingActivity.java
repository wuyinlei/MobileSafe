package com.example.mobilesafe.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.mobilesafe.R;
import com.example.mobilesafe.service.AddressService;
import com.example.mobilesafe.service.CallSafeService;
import com.example.mobilesafe.utils.ServiceStateUtils;
import com.example.mobilesafe.view.SettingClickView;
import com.example.mobilesafe.view.SettingItemView;

/**
 * Created by 若兰 on 2015/12/23.
 */
public class SettingActivity extends AppCompatActivity {

    private TextView tvtitle;
    private TextView tvdesc;
    private SettingItemView siv_update,siv_address,siv_callsafe;
    private SettingClickView scvStyle,scvLocation;
    private SharedPreferences mPrefs;
    int style;
    final String[]items = new String[]{"半透明","活力橙","卫士蓝","金属灰","苹果绿"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mPrefs = getSharedPreferences("config", MODE_PRIVATE);
        style = mPrefs.getInt("address_style", 0);  //读取保存的style
        initialize();
        initAddressService();
        initUpdate();
        initAddressStyle();
        initAddresssLocation();
        initCallSafe();
    }

    /**
     * 初始化控件
     */
    private void initialize() {
        tvtitle = (TextView) findViewById(R.id.tv_title);
        tvdesc = (TextView) findViewById(R.id.tv_desc);
        siv_update = (SettingItemView) findViewById(R.id.siv_update);
        siv_address = (SettingItemView) findViewById(R.id.siv_address);
        siv_callsafe = (SettingItemView) findViewById(R.id.siv_callsafe);
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
     * 初始化黑名单服务
     */
    private void initCallSafe(){

        //根据归属地服务是否运行来更新选择框
        boolean serviceRunning = ServiceStateUtils.isServiceRunning(this, "com.example.mobilesafe.service.CallSafeService");

        //判断该服务是否运行，如果没有运行，勾选框就会不勾选
        if (serviceRunning){
            siv_callsafe.setChecked(true);
        } else {
            siv_callsafe.setChecked(false);
        }
        siv_callsafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (siv_callsafe.isChecked()){
                    siv_callsafe.setChecked(false);
                    //停止归属地服务
                    stopService(new Intent(SettingActivity.this, CallSafeService.class));
                } else {
                    siv_callsafe.setChecked(true);
                    //开启归属地服务
                    startService(new Intent(SettingActivity.this, CallSafeService.class));
                }
            }
        });
    }

    /**
     * 初始化电话号码归属地的显示的服务
     */
    private void initAddressService() {

        //根据归属地服务是否运行来更新选择框
        boolean serviceRunning = ServiceStateUtils.isServiceRunning(this, "com.example.mobilesafe.service.AddressService");

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
                    stopService(new Intent(SettingActivity.this, AddressService.class));
                } else {
                    siv_address.setChecked(true);
                    //开启归属地服务
                    startService(new Intent(SettingActivity.this, AddressService.class));
                }
            }
        });
    }

    /**
     * 修改提示框的显示风格
     */
    private void initAddressStyle(){
        scvStyle = (SettingClickView) findViewById(R.id.scvStyle);
        scvStyle.setTitle("归属地提示框风格");
        scvStyle.setDesc(items[style]);
        scvStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSingleChooseDialgo();
            }
        });
    }

    /**
     * 弹出选择风格的单选框
     */
    private void showSingleChooseDialgo() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("归属地提示框风格");
        builder.setIcon(R.mipmap.ic_launcher);


        builder.setSingleChoiceItems(items, style, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPrefs.edit().putInt("address_style", which).commit(); //保存选择的风格
                scvStyle.setDesc(items[which]);  //更新组合控件的信息描述
                dialog.dismiss();   //让dialog消失
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    /**
     * 修改归属地显示的位置
     */
    private void initAddresssLocation(){
        scvLocation = (SettingClickView) findViewById(R.id.addressWhere);
        scvLocation.setTitle("归属地提示框显示位置");
        scvLocation.setDesc("设置归属地提示框的显示位置");
        scvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this,DragViewActivity.class));
            }
        });
    }
}
