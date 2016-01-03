package com.example.mobilesafe.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mobilesafe.R;
import com.example.mobilesafe.bean.APPinfo;
import com.example.mobilesafe.bean.TaskInfo;
import com.example.mobilesafe.engine.TaskInfoParser;
import com.example.mobilesafe.utils.SystemInfoUtils;

import java.util.ArrayList;
import java.util.List;

public class TaskManagerActivity extends AppCompatActivity {

    private TextView tvtaskprocesscount;
    private TextView tvtaskmemory;
    private ListView listview;

    private List<TaskInfo> taskInfos;

    private String availMem;
    private String totalMem;
    private long total;

    private TaskManagerAdapter taskManagerAdapter;

    /**
     * 用戶程序的集合
     */
    private ArrayList<TaskInfo> userTaskInfos;

    /**
     * 系統程序的集合
     */
    private ArrayList<TaskInfo> systemTaskInfos;

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


/*

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            taskManagerAdapter = new TaskManagerAdapter();
        }
    };
*/


    /**
     * 初始化数据
     * ActivityManager   --->任务管理器  进程管理器
     * <p/>
     * PackageManager   ---->包管理器
     */
    private void initData() {

        initTaskProcess();
        initMemInfo();

        new Thread() {
            @Override
            public void run() {
                super.run();
                taskInfos = TaskInfoParser.getTaskInfos(TaskManagerActivity.this);

                userTaskInfos = new ArrayList<>();
                systemTaskInfos = new ArrayList<>();
                for (TaskInfo taskInfo:taskInfos) {
                    //对所有的进程进行拆分，得到用户进程和系统进程
                    if (taskInfo.isUserApp()){
                        userTaskInfos.add(taskInfo);
                    } else {
                        systemTaskInfos.add(taskInfo);
                    }
                }

                //handler.sendEmptyMessage(0);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        taskManagerAdapter = new TaskManagerAdapter();
                        listview.setAdapter(taskManagerAdapter);
                    }
                });
            }
        }.start();

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


    private class TaskManagerAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return taskInfos.size() + 2;
        }

        @Override
        public Object getItem(int position) {
            if (position == 0) {
                return null;
            } else if (position == userTaskInfos.size() + 1) {
                return null;
            }

            TaskInfo taskInfo;

            if (position < userTaskInfos.size() + 1) {
                //減去多出來的特殊的條目
                taskInfo = userTaskInfos.get(position - 1);
            } else {
                int location = 1 + userTaskInfos.size() + 1;
                taskInfo = systemTaskInfos.get(position - location);
            }

            return taskInfo;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
/**
 * 在这里判断，思路---->   当我们添加了系统应用和用户应用的时候，那就说明我们增加了两个自定义的item，也就是说
 *                       我们的第一个不是和显示程序的界面一致，所以我们要自定义的TextView
 *                       然后在userAppInfos的数量的 + 1 的下一个位置是我们的系统程序的自定义的位置
 */
            if (position == 0) {
                TextView textView = new TextView(TaskManagerActivity.this);
                textView.setTextSize(25);
                textView.setText("用戶进程(" + userTaskInfos.size() + ")");
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            } else if (position == userTaskInfos.size() + 1) {
                TextView textView = new TextView(TaskManagerActivity.this);
                textView.setText("系統进程(" + systemTaskInfos.size() + ")");
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(25);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            }

            TaskInfo taskInfo;
            if (position < userTaskInfos.size() + 1) {
                //減去多出來的特殊的條目
                taskInfo = userTaskInfos.get(position - 1);
            } else {
                int location = 1 + userTaskInfos.size() + 1;
                taskInfo = systemTaskInfos.get(position - location);
            }

            ViewHolder holder = new ViewHolder();
            View view = View.inflate(TaskManagerActivity.this, R.layout.item_task_manager, null);

            holder.ivicon = (ImageView) view.findViewById(R.id.iv_icon);
            holder.tvName = (TextView) view.findViewById(R.id.tvName);
            holder.tvappmemorysize = (TextView) view.findViewById(R.id.tv_app_memory_size);
            holder.tvappsize = (CheckBox) view.findViewById(R.id.tv_app_size);

            holder.ivicon.setImageDrawable(taskInfo.getIcon());
            holder.tvName.setText(taskInfo.getAppName());
            holder.tvappmemorysize.setText(Formatter.formatFileSize(TaskManagerActivity.this,taskInfo.getMemorySize()));


           // holder.tvappsize

            return view;
        }

    }

    class ViewHolder {

        ImageView ivicon;
        TextView tvName;
        TextView tvappmemorysize;
        CheckBox tvappsize;
    }
}
