package com.example.mobilesafe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mobilesafe.R;

/**
 * Created by 若兰 on 2015/12/23.
 */
public class HomeAdapter extends BaseAdapter {

    /**
     * TextView数组，用来存储text标题
     */
    private String[] mItems = new String[]{"手机防盗", "通讯卫士", "软件管理", "进程管理", "流量统计",
            "手机杀毒", "缓存清理", "高级工具", "设置中心"};

    /**
     * 图片数组，用来存储图片
     */
    private int[] mPics = new int[]{R.mipmap.home_safe, R.mipmap.home_callmsgsafe
            , R.mipmap.home_apps, R.mipmap.home_taskmanager, R.mipmap.home_netmanager, R.mipmap.home_trojan
            , R.mipmap.home_sysoptimize, R.mipmap.home_tools, R.mipmap.home_settings};
    /**
     * 上下文
     */
    private Context mContext;

    /**
     * 布局加载器
     */
    private LayoutInflater mInflater;

    public HomeAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mItems.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.home_list_item, parent, false);
        ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
        TextView tv_item = (TextView) view.findViewById(R.id.tv_item);
        tv_item.setText(mItems[position]);
        iv_icon.setImageResource(mPics[position]);
        return view;
    }
}
