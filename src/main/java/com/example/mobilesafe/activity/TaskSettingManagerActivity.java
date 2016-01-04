package com.example.mobilesafe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import com.example.mobilesafe.R;
import com.example.mobilesafe.service.KillProcessService;
import com.example.mobilesafe.utils.SystemInfoUtils;


/**
 * 任务管理器的设置界面
 */
public class TaskSettingManagerActivity extends AppCompatActivity {

    private CheckBox checkstatus, check_kill_process;
    private RelativeLayout showrl, killrl;
    private SharedPreferences mPres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_setting_manager);
        //0表示私有的模式

        initialize();
    }

    /**
     * 初始化控件
     */
    private void initialize() {

        mPres = getSharedPreferences("config", 0);
        checkstatus = (CheckBox) findViewById(R.id.check_status);
        showrl = (RelativeLayout) findViewById(R.id.show_rl);
        killrl = (RelativeLayout) findViewById(R.id.kill_rl);
        check_kill_process = (CheckBox) findViewById(R.id.check_kill_process);

        initData();
        killProcess();
    }

    /**
     * 初始化数据
     * <p/>
     * 是否显示系统进程的逻辑
     */
    private void initData() {
        checkstatus.setChecked(mPres.getBoolean("is_show_system", false));  //默认显示

        showrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkstatus.isChecked()) {
                    checkstatus.setChecked(!checkstatus.isChecked());

                    //如果选中给true
                    SharedPreferences.Editor editor = mPres.edit();
                    editor.putBoolean("is_show_system", false);
                    editor.commit();
                } else {
                    checkstatus.setChecked(!checkstatus.isChecked());
                    //没有选中就给fasle
                    SharedPreferences.Editor editor = mPres.edit();
                    editor.putBoolean("is_show_system", true);
                    editor.commit();
                }
            }
        });
    }

    /**
     * 是否定时杀死后台进程的逻辑
     */
    private void killProcess() {

        final Intent intent = new Intent(this, KillProcessService.class);
        killrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_kill_process.isChecked()) {
                    check_kill_process.setChecked(!check_kill_process.isChecked());
                    startService(intent);
                } else {
                    check_kill_process.setChecked(!check_kill_process.isChecked());
                    startService(intent);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
       if (SystemInfoUtils.isServiceRunning(TaskSettingManagerActivity.this,"com.example.mobilesafe.service")){
            check_kill_process.setChecked(true);
       } else {
           check_kill_process.setChecked(false);
       }
    }
}
