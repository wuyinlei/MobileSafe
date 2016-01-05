package com.example.mobilesafe.fragment;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mobilesafe.R;
import com.example.mobilesafe.bean.APPinfo;
import com.example.mobilesafe.db.AppLockDao;
import com.example.mobilesafe.engine.AppInfos;

import java.util.ArrayList;
import java.util.List;

public class UnLockFragment extends Fragment {

    private ListView list_view;
    private TextView tv_unlock;
    private View view;
    private List<APPinfo> appInfos;
    private UnlockAdapter adapter;
    private AppLockDao dao;

    /**
     * 初始化一个没有加锁的集合
     */
    private ArrayList<APPinfo> unLockList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_un_lock, container, false);
            list_view = (ListView) view.findViewById(R.id.list_view);
            tv_unlock = (TextView) view.findViewById(R.id.tv_unlock);

            appInfos = AppInfos.getAppInfos(getActivity());

            //获取到程序锁的dao
            dao = new AppLockDao(getActivity());

            unLockList = new ArrayList<>();


            //unLockList.clear();
            //unLockList.clear();
            for (APPinfo appInfo : appInfos) {
                //判断当前的应用是否在数据库里面
                if (dao.find(appInfo.getApkPageName())) {

                } else {
                    //如果查询不到就是没有在加锁的程序里面

                    unLockList.add(appInfo);
                }
            }


            adapter = new UnlockAdapter();
            list_view.setAdapter(adapter);

        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
/*
        appInfos = AppInfos.getAppInfos(getActivity());

        //获取到程序锁的dao
        dao = new AppLockDao(getActivity());

        unLockList = new ArrayList<>();


        //unLockList.clear();
        //unLockList.clear();
        for (APPinfo appInfo : appInfos) {
            //判断当前的应用是否在数据库里面
            if (dao.find(appInfo.getApkPageName())) {

            } else {
                //如果查询不到就是没有在加锁的程序里面

                unLockList.add(appInfo);
            }
        }


        adapter = new UnlockAdapter();
        list_view.setAdapter(adapter);*/

    }

    private class UnlockAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            tv_unlock.setText("未加锁" + unLockList.size() + "个");
            return unLockList.size();
        }

        @Override
        public Object getItem(int position) {
            return unLockList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            final View view;
            final APPinfo apPinfo;
            if (convertView == null) {
                view = View.inflate(getActivity(), R.layout.item_unlock_list_view, null);

                holder = new ViewHolder();
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                holder.iv_unlock = (ImageView) view.findViewById(R.id.lock_app);


                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            holder.iv_icon.setImageDrawable(unLockList.get(position).getIcon());
            holder.tv_name.setText(unLockList.get(position).getApkName());

            apPinfo = unLockList.get(position);

            holder.iv_unlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //添加到程序锁数据库


                    //初始化一个位移动画
                    TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1.0f,
                            Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);

                    //设置动画时间
                    animation.setDuration(5000);
                    view.startAnimation(animation);


                    new Thread() {
                        @Override
                        public void run() {
                            SystemClock.sleep(5000);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dao.addApp(apPinfo.getApkPageName());
                                    //从未加锁里面移除
                                    unLockList.remove(position);

                                    //刷新界面
                                    adapter.notifyDataSetChanged();

                                }
                            });
                        }
                    }.start();
/*
                    //从未加锁里面移除
                    unLockList.remove(position);

                    //刷新界面
                    adapter.notifyDataSetChanged();
*/


                }
            });

            return view;
        }
    }

    class ViewHolder {

        private ImageView iv_icon;
        private TextView tv_name;
        private ImageView iv_unlock;
    }
}
