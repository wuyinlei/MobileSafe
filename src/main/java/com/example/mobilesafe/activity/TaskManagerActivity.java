package com.example.mobilesafe.activity;

import android.app.ActivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mobilesafe.R;
import com.example.mobilesafe.utils.SystemInfoUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.List;

public class TaskManagerActivity extends AppCompatActivity {

    private TextView tvtaskprocesscount;
    private TextView tvtaskmemory;
    private ListView listview;

    private String availMem;
    private String totalMem;
    private long total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);
        initialize();
        initData();
    }


    /**
     * 初始化控件
     */
    private void initialize() {

        tvtaskprocesscount = (TextView) findViewById(R.id.tv_task_process_count);
        tvtaskmemory = (TextView) findViewById(R.id.tv_task_memory);
        listview = (ListView) findViewById(R.id.list_view);
    }

    /**
     * 初始化数据
     * ActivityManager   --->任务管理器  进程管理器
     * <p/>
     * PackageManager   ---->包管理器
     */
    private void initData() {
        initTaskProcess();


        initMemInfo();

    }

    /**
     * 获取到进程方面有关的信息
     */
    private void initTaskProcess() {
      /*  //得到进程的管理者(知道安装了多少app，可以获取进程数)
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        //获取当前手机上面的所有运行的进程
        runningAppProcesses = activityManager.getRunningAppProcesses();
        int size = runningAppProcesses.size();*/
        int count = SystemInfoUtils.getProcessCount(this);
        tvtaskprocesscount.setText("运行中的进程 ：" + count + "个");
    }

    /**
     * 获取到内存有关的信息
     */
    private void initMemInfo() {
       /* ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        //获取到没存的基本信息
        activityManager.getMemoryInfo(memoryInfo);

        //获取到剩余内存
        availMem = Formatter.formatFileSize(TaskManagerActivity.this, memoryInfo.availMem).substring(0, 4);
*/
        availMem = SystemInfoUtils.getAvailMem(this);
        //获取到总的内存
       /* *//**
         * 这个是api 16及以上的使用的
         *
         * "/proc/meminfo"   这个是文件中存储的，是用下面的这个获取到的内存
         * 就不会有什么API版本不支持的问题
         *
         * MemTotal:    2541251KB
         *
         * 也就是说不能再API 16以下的手机上运用
         *//*
        //String totalMem = Formatter.formatFileSize(TaskManagerActivity.this,memoryInfo.totalMem);

        long total = 0;

        try {
            //  /proc/meminfo  配置文件的路径
            FileInputStream fis = new FileInputStream(new File("/proc/meminfo"));

            //每次读取一行
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String readLine = reader.readLine();
            StringBuffer sb = new StringBuffer();
            for (char c : readLine.toCharArray()) {
                //装换成字符数组
                if (c >= '0' && c <= '9') {
                    sb.append(c);
                }
            }

            total = Long.parseLong(sb.toString()) * 1024;

        } catch (Exception e) {
            e.printStackTrace();
        }*/

        total = SystemInfoUtils.getTotalMem(this);

        totalMem = Formatter.formatFileSize(TaskManagerActivity.this, total);

        tvtaskmemory.setText("剩余/总内存 : " + availMem + "/" + totalMem);
    }

}
