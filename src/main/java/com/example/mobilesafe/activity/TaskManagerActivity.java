package com.example.mobilesafe.activity;

import android.app.ActivityManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafe.R;
import com.example.mobilesafe.bean.TaskInfo;
import com.example.mobilesafe.engine.TaskInfoParser;
import com.example.mobilesafe.utils.SystemInfoUtils;
import com.example.mobilesafe.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        taskManagerAdapter = new TaskManagerAdapter();
        initialize();
        initData();
        initialize();
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
                for (TaskInfo taskInfo : taskInfos) {
                    //对所有的进程进行拆分，得到用户进程和系统进程
                    if (taskInfo.isUserApp()) {
                        userTaskInfos.add(taskInfo);
                    } else {
                        systemTaskInfos.add(taskInfo);
                    }
                }

                //handler.sendEmptyMessage(0);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        listview.setAdapter(taskManagerAdapter);
                        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        });
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

    /**
     * 全选
     *
     * @param view
     */
    public void selectAll(View view) {

        for (TaskInfo taskInfo : userTaskInfos) {

            //判断当前的用户程序是否是自己的程序，如果是自己的程序，就把文本框隐藏了
            if (taskInfo.getPackageName().equals(getPackageName())){
                continue;
            }

            taskInfo.setChecked(true);
        }
        for (TaskInfo taskInfo : systemTaskInfos) {
            taskInfo.setChecked(true);
        }
        //对当前的额list进行刷新，要不然只能在次进去的时候才能看到结果
        taskManagerAdapter.notifyDataSetChanged();

    }

    /**
     * 反选
     *
     * @param view
     */
    public void selectNone(View view) {
        for (TaskInfo taskInfo : userTaskInfos) {

            //判断当前的用户程序是否是自己的程序，如果是自己的程序，就把文本框隐藏了
            if (taskInfo.getPackageName().equals(getPackageName())){
                continue;
            }

            taskInfo.setChecked(!taskInfo.isChecked());
        }
        for (TaskInfo taskInfo : systemTaskInfos) {
            taskInfo.setChecked(!taskInfo.isChecked());
        }
        //对当前的额list进行刷新，要不然只能在次进去的时候才能看到结果
        taskManagerAdapter.notifyDataSetChanged();
    }

    /**
     * 清理进程
     *
     * @param view
     */
    public void killProcess(View view) {

        //想杀死进程，要得到进程管理器
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        //清理进程的集合
        List<TaskInfo> killList = new ArrayList<>();

        //清理的总共的进程个数
        int totalCount = 0;

        //清理的多少的内存
        int availMemo = 0;

        
        for (TaskInfo taskInfo:userTaskInfos) {
            if (taskInfo.isChecked()){
                //杀死进程  参数是包名
                killList.add(taskInfo);
                //userTaskInfos.remove(taskInfo);
                availMemo += taskInfo.getMemorySize();
                totalCount++;
            }
        }

        for (TaskInfo taskInfo:systemTaskInfos) {
            if (taskInfo.isChecked()){
                killList.add(taskInfo);
                //systemTaskInfos.remove(taskInfo);
                availMemo += taskInfo.getMemorySize();
                totalCount++;
            }
        }

        /**
         * 在集合迭代的时候不能更改集合的大小，解决方法是在整一个集合来装他
         *
         * 然后在去remove掉
         */
        for (TaskInfo taskInfo:killList) {
            if (taskInfo.isUserApp()){
                //判断是否是用户app
                userTaskInfos.remove(taskInfo);
                activityManager.killBackgroundProcesses(taskInfo.getPackageName());

            } else {
                systemTaskInfos.remove(taskInfo);
                activityManager.killBackgroundProcesses(taskInfo.getPackageName());

            }
        }

        //刷新界面
        taskManagerAdapter.notifyDataSetInvalidated();

        UIUtils.showToast(this, "一共清理了" + totalCount + "个进程," +
                "释放了" + Formatter.formatFileSize(this, availMemo) + "内存");

        int count = SystemInfoUtils.getProcessCount(this);
        tvtaskprocesscount.setText("运行中的进程 ：" + count + "个");

        availMem = SystemInfoUtils.getAvailMem(this)  ;


        total = SystemInfoUtils.getTotalMem(this);
        totalMem = Formatter.formatFileSize(TaskManagerActivity.this, total);

        tvtaskmemory.setText("剩余/总内存 : " + availMem + "/" + totalMem);

    }

    /**
     * 设置
     *
     * @param view
     */
    public void setApp(View view) {

    }


    public class TaskManagerAdapter extends BaseAdapter {


        @Override
        public int getCount() {

            //因为加入了两个特殊的条目
            return userTaskInfos.size() + 1 + systemTaskInfos.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            if (position == 0) {
                //这个地方是用户进程提示
                return null;
            } else if (position == userTaskInfos.size() + 1) {
                //这个地方是系统进程提示
                return null;
            }
            //下面的是系统进程和用户进程

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

            final TaskInfo taskInfo;
            if (position < userTaskInfos.size() + 1) {
                //減去多出來的特殊的條目
                taskInfo = userTaskInfos.get(position - 1);
            } else {
                int location = 1 + userTaskInfos.size() + 1;
                taskInfo = systemTaskInfos.get(position - location);
            }
            ViewHolder holder = new ViewHolder();
            View view;
            if (convertView != null && convertView instanceof LinearLayout) {

                view = convertView;
                holder = (ViewHolder) view.getTag();

            } else {

                view = View.inflate(TaskManagerActivity.this, R.layout.item_task_manager, null);
                holder.ivicon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tvName = (TextView) view.findViewById(R.id.tvName);
                holder.tvappmemorysize = (TextView) view.findViewById(R.id.tv_app_memory_size);
                holder.check_process = (CheckBox) view.findViewById(R.id.tv_app_size);

                view.setTag(holder);
            }


            holder.ivicon.setImageDrawable(taskInfo.getIcon());
            holder.tvName.setText(taskInfo.getAppName());
            String memSize = Formatter.formatFileSize(TaskManagerActivity.this, taskInfo.getMemorySize());
            holder.tvappmemorysize.setText("内存占用 ： " + memSize);

            //在这里要给checkbox设置一个初始值，要不然的话在全选的时候不会有效果的
            holder.check_process.setChecked(taskInfo.isChecked());

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Object obj = listview.getItemAtPosition(position);
                    if (obj instanceof TaskInfo && obj != null) {
                        TaskInfo taskinfo = (TaskInfo) obj;

                        ViewHolder ho = (ViewHolder) view.getTag();
                        //判断当前的item是否被勾选上
                        if (taskinfo.isChecked()) {
                            taskinfo.setChecked(false);
                            ho.check_process.setChecked(false);
                        } else {
                            taskinfo.setChecked(true);
                            ho.check_process.setChecked(true);
                        }
                    }
                }
            });

            return view;
        }

    }

    class ViewHolder {

        ImageView ivicon;
        TextView tvName;
        TextView tvappmemorysize;
        CheckBox check_process;
    }
}
