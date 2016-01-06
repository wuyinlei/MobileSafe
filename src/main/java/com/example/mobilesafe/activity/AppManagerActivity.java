package com.example.mobilesafe.activity;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.mobilesafe.R;
import com.example.mobilesafe.bean.APPinfo;
import com.example.mobilesafe.engine.AppInfos;

import java.util.ArrayList;
import java.util.List;

/**
 * 手机应用管理
 */
public class AppManagerActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvRom;
    private TextView tvSdk;
    private ListView listview;
    private TextView tv_app;
    private LinearLayout ll_uninstall, ll_run, ll_share, ll_detail;
    private APPinfo clickAppInfo;
    private UninstallReceiver uninstallReceiver;

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
    private PopupWindow popupWindow;
    private Object obj;

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
        tv_app = (TextView) findViewById(R.id.tv_app);

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

        //设置ListView的滚动状态
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            /**
             *                          在这实现了在滑动的时候，判断当前的ListView是属于
             *                          系统应用程序还是用户应用程序的，如果是系统应用程序的
             *                          就显示系统应用程序（X个）   否则就显示用户程序（X个）
             * @param view
             * @param firstVisibleItem   第一个可见的条目的位置
             * @param visibleItemCount   一页可以展示的多少个条目
             * @param totalItemCount     总共的Item的个数
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                //在滚动的时候也要消失窗口
                popupWindowDismiss();

                if (userAppInfos != null && systemAppInfos != null) {
                    if (firstVisibleItem > userAppInfos.size() + 1) {
                        //系统应用程序
                        tv_app.setText("系统程序(" + systemAppInfos.size() + "个)");
                    } else {
                        //用户应用程序
                        tv_app.setText("用户程序(" + userAppInfos.size() + "个)");
                    }
                }
            }
        });

        /**
         * 设置ListView的点击监听事件
         */
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取到当前点击的Item的对象
                obj = listview.getItemAtPosition(position);
                if (obj != null && obj instanceof APPinfo) {
                    clickAppInfo = (APPinfo) obj;
                    View contentView = View.inflate(AppManagerActivity.this, R.layout.popup_item, null);
                    //-2 表示包裹内容   也就wrap_content的一个值
                    /**
                     * <p>A popup window that can be used to display an arbitrary view. The popup
                     * window is a floating container that appears on top of the current
                     * activity.</p>
                     *
                     * @see android.widget.AutoCompleteTextView
                     * @see android.widget.Spinner
                     */

                    popupWindowDismiss();

                    ll_uninstall = (LinearLayout) contentView.findViewById(R.id.ll_uninstall);
                    ll_uninstall.setOnClickListener(AppManagerActivity.this);
                    ll_run = (LinearLayout) contentView.findViewById(R.id.ll_run);
                    ll_run.setOnClickListener(AppManagerActivity.this);
                    ll_share = (LinearLayout) contentView.findViewById(R.id.ll_share);
                    ll_share.setOnClickListener(AppManagerActivity.this);
                    ll_detail = (LinearLayout) contentView.findViewById(R.id.ll_detail);
                    ll_detail.setOnClickListener(AppManagerActivity.this);

                    popupWindow = new PopupWindow(contentView, -2, -2);
                    //使用当前的PopupWindow，必须设置背景，要不然没有动画
                    //需要注意：使用PopupWindow 必须设置背景。不然没有动画
                    popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //
                    int[] location = new int[2];
                    //获取到view展示在窗体上面的位置
                    view.getLocationInWindow(location);

                    popupWindow.showAtLocation(parent, Gravity.LEFT + Gravity.TOP, 90, location[1]);

                    //得到一个缩放的动画
                    ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    sa.setDuration(1000);

                    AlphaAnimation aa = new AlphaAnimation(0, 1);
                    contentView.startAnimation(sa);
                    contentView.startAnimation(aa);
                }


            }
        });

        uninstallReceiver = new UninstallReceiver();

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        registerReceiver(uninstallReceiver, intentFilter);

    }

    /**
     * 在点击的时候判断是否有窗口在显示，如果有显示就dismiss掉
     */
    private void popupWindowDismiss() {

        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
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


    @Override
    protected void onResume() {
        super.onResume();
    }

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /**
             * 卸载
             */
            case R.id.ll_uninstall:
                AppUninstall();
                break;

            /**
             * 运行
             */
            case R.id.ll_run:
                AppRun();
                break;

            /**
             * 分享
             */
            case R.id.ll_share:
                AppShare();
                break;

            /**
             * 详情
             */
            case R.id.ll_detail:
                AppDetail();
                break;
        }
    }

    /**
     * 卸载程序的功能，触及到这个方法的时候，会去卸载改程序
     */
    private void AppUninstall() {
        Intent uninstall_localIntent = new Intent("android.intent.action.DELETE", Uri.parse("package:" + clickAppInfo.getApkPageName()));
        startActivity(uninstall_localIntent);
        popupWindowDismiss();
    }

    /**
     * 程序运行方法
     */
    private void AppRun() {
        Intent localIntent = getPackageManager().getLaunchIntentForPackage(clickAppInfo.getApkPageName());
        this.startActivity(localIntent);
        popupWindowDismiss();
    }

    /**
     * 应用程序分享页面，当触及这个功能的时候，会调用系统的短信功能或者蓝牙功能，来实现分享app功能
     */
    private void AppShare() {
        Intent shareIntent = new Intent("android.intent.action.SEND");
        shareIntent.setType("text/plain");
        shareIntent.putExtra("android.intent.extra.SUBJECT", "f分享");
        shareIntent.putExtra("android.intent.extra.TEXT", "Hi！推荐您使用软件：" + clickAppInfo.getApkName() + "下载地址:" + "https://play.google.com/store/apps/details?id=" + clickAppInfo.getApkPageName());
        this.startActivity(shareIntent);
        popupWindowDismiss();
    }

    /**
     * 应用程序详情页面
     */
    private void AppDetail() {
        Intent detail_intent = new Intent();
        detail_intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        detail_intent.addCategory(Intent.CATEGORY_DEFAULT);
        detail_intent.setData(Uri.parse("package:" + clickAppInfo.getApkPageName()));
        startActivity(detail_intent);
        popupWindowDismiss();
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

            /**
             * 用下面的代码，来返回一个APPinfo对象，也就是返回的你要当前你要点击的item
             *
             * 因为接下来我们要使用popupWindow在点击item的时候，弹出popupWindow窗口
             *
             * 以实现我们的对程序的运行、卸载、详情、分享等功能
             *
             * 因为这个地方我们加入了两个特殊的条目，所以我们要做一下处理，以防止返回的都是一些我们
             *
             * 需要的数据，如果我们没有加入特殊的条目，就是一些基础的条目，我们可以直接
             *
             * return appInfos.get(position);
             */
            if (position == 0) {
                return null;
            } else if (position == userAppInfos.size() + 1) {
                return null;
            }

            APPinfo apPinfo;

            if (position < userAppInfos.size() + 1) {
                //減去多出來的特殊的條目
                apPinfo = userAppInfos.get(position - 1);
            } else {
                int location = 1 + userAppInfos.size() + 1;
                apPinfo = systemAppInfos.get(position - location);
            }

            return apPinfo;
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


    private class UninstallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // Log.d("UninstallReceiver", "接收到卸载的广播");
            initData();
        }
    }

    /**
     * 在後退的時候刪除彈出的窗體
     */
    @Override
    protected void onDestroy() {
        popupWindowDismiss();
        super.onDestroy();
        unregisterReceiver(uninstallReceiver);
    }
}
