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

public class LockingFragment extends Fragment {


    private TextView tvunlock;
    private ListView listview;
    private View view;
    private LockAdapter adapter;
    private AppLockDao dao;
    private List<APPinfo> appInfos;
    private ArrayList<APPinfo> lockLists;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_locking, container, false);
            tvunlock = (TextView) view.findViewById(R.id.tv_unlock);
            listview = (ListView) view.findViewById(R.id.list_view);


           /* lockLists = new ArrayList<>();

            dao = new AppLockDao(getActivity());
            appInfos = AppInfos.getAppInfos(getActivity());
            for (APPinfo appInfo:appInfos) {
                //如果能找到当前的包名，说明在当前的程序锁的数据库中
                if (dao.find(appInfo.getApkPageName())){
                    lockLists.add(appInfo);
                } else {
                }
            }
            adapter = new LockAdapter();

            listview.setAdapter(adapter);*/
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        //拿到所有的应用程序
        List<APPinfo> appInfos = AppInfos.getAppInfos(getActivity());
        //初始化一个加锁的集合

        lockLists = new ArrayList<APPinfo>();


        dao = new AppLockDao(getActivity());
        for (APPinfo appInfo : appInfos) {
            //如果能找到当前的包名说明在程序锁的数据库里面
            if(dao.find(appInfo.getApkPageName())){
                lockLists.add(appInfo);
            }else{

            }
        }
        adapter = new LockAdapter();
        listview.setAdapter(adapter);

    }

    private class LockAdapter  extends BaseAdapter{

        @Override
        public int getCount() {
            tvunlock.setText("已经加锁(" + lockLists.size() + ")个");
            return lockLists.size();
        }

        @Override
        public Object getItem(int position) {
            return lockLists.get(position);
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

            apPinfo = lockLists.get(position);

            holder.iv_icon.setImageDrawable(apPinfo.getIcon());
            holder.tv_name.setText(apPinfo.getApkName());


            holder.iv_unlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF
                            , 0, Animation.RELATIVE_TO_SELF
                            , -1.0f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF
                            , 0);
                    animation.setDuration(5000);
                    view.startAnimation(animation);

                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            SystemClock.sleep(5000);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dao.delete(apPinfo.getApkPageName());
                                    lockLists.remove(position);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }.start();

                }
            });

            return view;
        }
    }

    static class ViewHolder {

        private ImageView iv_icon;
        private TextView tv_name;
        private ImageView iv_unlock;
    }
 }
