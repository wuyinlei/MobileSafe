package com.example.mobilesafe.activity;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mobilesafe.R;
import com.example.mobilesafe.bean.APPinfo;
import com.example.mobilesafe.engine.AppInfos;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

public class AppManagerActivity extends AppCompatActivity {

    private TextView tvRom;
    private TextView tvSdk;
    private ListView listview;

    /**
     * rom的剩余空间
     */
    private long romFreeSpace;

    /**
     * SD卡的剩余空间
     */
    private long sdFreeSpace;

    /**
     * 所有程序的集合
     */
    private List<APPinfo> appInfos;

    /**
     * 用戶程序的集合
     */
    private ArrayList<APPinfo> userAppInfos;

    /**
     * 系統程序的集合
     */
    private ArrayList<APPinfo> systemAppInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        initialize();
        initData();
        initAppData();
    }

    /**
     * 初始化控件
     */
    private void initialize() {

        tvRom = (TextView) findViewById(R.id.tvRom);
        tvSdk = (TextView) findViewById(R.id.tvSdk);
        listview = (ListView) findViewById(R.id.list_view);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //获取到运行的剩余空间
        romFreeSpace = Environment.getDataDirectory().getFreeSpace();
        //获取到额外的剩余空间  SD卡
        sdFreeSpace = Environment.getExternalStorageDirectory().getFreeSpace();

        //在这里用到了格式化，可以格式化成文本类型
        tvRom.setText("内存可用 ：" + android.text.format.Formatter.formatFileSize(this, romFreeSpace));
        tvSdk.setText("SD卡可用 ：" + android.text.format.Formatter.formatFileSize(this, sdFreeSpace));

        userAppInfos = new ArrayList<>();
        systemAppInfos = new ArrayList<>();

    }

    /**
     * 新开一个线程来接受消息
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AppManagerAdapter adapter = new AppManagerAdapter();
            listview.setAdapter(adapter);
        }
    };


    /**
     * 初始化app的资源信息
     */
    private void initAppData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                //获取到所有安装到手机上面的应用程序
                appInfos = AppInfos.getAppInfos(AppManagerActivity.this);

                //把這個appInfos一分為二，  用戶程序和系統程序的集合


                for (APPinfo appInfo : appInfos) {
                    if (appInfo.isUserApp()) {
                        //判断是第三方应用的信息，然后添加到userAppInfos数组里面
                        userAppInfos.add(appInfo);
                    } else {
                        //判断是系统应用的信息，然后添加到systemAppInfos数组里面
                        systemAppInfos.add(appInfo);
                    }
                }

                mHandler.sendEmptyMessage(0);
            }
        }.start();

    }


    /**
     * ListView的适配器
     */
    private class AppManagerAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            //在这里因为我们添加了两个特殊的item，所以这个地方要返回的时候要加上我们私自添加的item的数量
            return userAppInfos.size() + 1 + systemAppInfos.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            return appInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //如果当前的position = 0，表示应用程序
            /**
             * 在这里判断，思路---->   当我们添加了系统应用和用户应用的时候，那就说明我们增加了两个自定义的item，也就是说
             *                       我们的第一个不是和显示程序的界面一致，所以我们要自定义的TextView
             *                       然后在userAppInfos的数量的 + 1 的下一个位置是我们的系统程序的自定义的位置
             */
            if (position == 0) {
                TextView textView = new TextView(AppManagerActivity.this);
                textView.setTextSize(25);
                textView.setText("用戶程序(" + userAppInfos.size() + ")");
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            } else if (position == userAppInfos.size() + 1) {
                TextView textView = new TextView(AppManagerActivity.this);
                textView.setText("系統程序(" + systemAppInfos.size() + ")");
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(25);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            }

            APPinfo apPinfo;

            /**
             * 在这里，因为我们添加了一个用户程序的item。所以我们如果在按照之前的get（position）去取值的话，那么我们第一个
             * 取得值使我们自己定义的  “用户程序”，而不是我们想要的程序的应用的信息，所以我们
             *          也就是说假如我们第一个程序是   搜狗输入法    那么我们取得应该是 0 位置的，但是实际上因为多了一个我们自定义的
             *                                                  那么，我们索取的是 1 位置上的，也就是显示的不对了，那么我们就要减去 1
             *
             */
            if (position < userAppInfos.size() + 1) {
                //減去多出來的特殊的條目
                apPinfo = userAppInfos.get(position - 1);
            } else {

                /**
                 * 在这里，当要显示系统程序的时候，我们这里的逻辑是这样的
                 *                      我们首先要得到之前有了多少的位置，加上我们自己定义的
                 *                      数量是  int location = 1 + userAppInfos.size() + 1;
                 *                      所以我们去取值的时候，我们要用得到的position - location才可以
                 *                      取到我们的系统程序的信息
                 */
                int location = 1 + userAppInfos.size() + 1;
                apPinfo = systemAppInfos.get(position - location);
            }

            ViewHolder holder;

            /**
             * 在这里，我们判断了这个converView的类型是否是linearLayout类型的
             * 如果不是的话，就加载布局，因为我们有了两个自定义的view，如果不这样做判断，会出错的
             * 也就是，哪两个我们自定义的TextView他的类型不是LinearLayout的，也就进不来了
             * 以至于我们加载的所有的布局都是属于我们的LinearLayout的
             */
            if (convertView == null && convertView instanceof LinearLayout) {

                holder = (ViewHolder) convertView.getTag();

            } else {

                convertView = View.inflate(AppManagerActivity.this, R.layout.item_app_manager, null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
                holder.tvLocal = (TextView) convertView.findViewById(R.id.tvLocal);
                holder.tvSize = (TextView) convertView.findViewById(R.id.tvSize);

                convertView.setTag(holder);

            }
            //设置资源
            holder.iv_icon.setBackground(apPinfo.getIcon());
            holder.tvName.setText(apPinfo.getApkName());
            if (apPinfo.isRom()) {
                holder.tvLocal.setText("手机内存");
            }
            //在这里我们使用了android.text.format.Formatter.formatFileSize()对一个long型的数进行了格式化成text格式  GB、MB、KB
            holder.tvSize.setText(android.text.format.Formatter.formatFileSize(AppManagerActivity.this, apPinfo.getApkSize()));

            return convertView;
        }

        class ViewHolder {
            private ImageView iv_icon;
            private TextView tvName, tvLocal, tvSize;
        }
    }
}
