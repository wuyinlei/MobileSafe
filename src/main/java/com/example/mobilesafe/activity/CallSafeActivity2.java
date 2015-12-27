package com.example.mobilesafe.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafe.R;
import com.example.mobilesafe.adapter.MyBaseAdapter;
import com.example.mobilesafe.bean.BlackNumberInfo;
import com.example.mobilesafe.db.BlackNumberDao;

import java.util.ArrayList;
import java.util.List;

public class CallSafeActivity2 extends AppCompatActivity {

    private ListView listView;
    private CallSafeAdapter adapter;
    private List<BlackNumberInfo> lists;
    private BlackNumberDao dao;
    private LinearLayout li_pd;
    private TextView tvPageNumber;
    private EditText etPageNumber;

    //一共有多少页
    private int totalPage;
    //当前页面
    private int mCurrentPage = 0;

    //每页展示20条数据
    private int pageSize = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_safe);
        lists = new ArrayList<>();
        dao = new BlackNumberDao(this);
        initialize();
        initData();
    }


    /**
     * 初始化布局控件
     */
    private void initialize() {

        li_pd = (LinearLayout) findViewById(R.id.li_pd);
        li_pd.setVisibility(View.VISIBLE);  //展示加载的圆圈
        listView = (ListView) findViewById(R.id.listView);
        etPageNumber = (EditText) findViewById(R.id.etPageNumber);
        tvPageNumber = (TextView) findViewById(R.id.tvPageNumber);
    }

    /**
     * 通过Handler异步通信，新开启子线程来查找数据（工作中的数据是很多的，如果在UI线程中更新的话会导致性能下降的）
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            //得到总得页数
            totalPage = dao.getTotal() / pageSize + 1;   //在这里我们要+1，因为我们取的是模
            String size = (mCurrentPage + 1) + "/" + totalPage;
            tvPageNumber.setText(size);   //通过总的记录数/每一页的数据
            li_pd.setVisibility(View.INVISIBLE);   //设置进度条不可见
            adapter = new CallSafeAdapter(lists, CallSafeActivity2.this);
            listView.setAdapter(adapter);
        }
    };

    /**
     * 初始化数据
     */
    private void initData() {

        new Thread() {
            @Override
            public void run() {
                super.run();
                //lists = dao.findAll();

                //在这里通过findPage传入当前页数和每页的数量的多少来获得数据
                lists = dao.findPage(mCurrentPage, pageSize);

                //获得完数据后发送message，通知线程做处理
                mHandler.sendEmptyMessage(0);
            }
        }.start();

    }

    private class CallSafeAdapter extends MyBaseAdapter<BlackNumberInfo> {
        public CallSafeAdapter(List lists, Context mContext) {
            super(lists, mContext);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if (convertView == null) {   //在这里判断view是否为空，如果是空的，则重新创建，这样就能保证
                //每一页有多少数据，我就加载多少数据，这样就优化了之前的，总共有多少数据，我就一下子加载了
                //有效的避免了OOM问题
                convertView = View.inflate(CallSafeActivity2.this, R.layout.item_call_safe, null);
                holder = new ViewHolder();
                holder.tvNumber = (TextView) convertView.findViewById(R.id.tvNumber);
                holder.tvMode = (TextView) convertView.findViewById(R.id.tvMode);
                holder.delete = (ImageView) convertView.findViewById(R.id.delete);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final BlackNumberInfo info = lists.get(position);
            holder.tvNumber.setText(lists.get(position).getNumber());
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String number = info.getNumber();
                    boolean result = dao.delete(number);
                    if (result) {
                        Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                        lists.remove(info);

                        //调用下面这一句是刷新界面
                        adapter.notifyDataSetChanged();
                        Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            String mode = lists.get(position).getMode();
            if (mode.equals("1")) {
                mode = "来电、短信拦截";
            } else if (mode.equals("2")) {
                mode = "电话拦截";
            } else if (mode.equals("3")) {
                mode = "短信拦截";
            }
            holder.tvMode.setText(mode);

            return convertView;
        }


    }

    static class ViewHolder {
        private TextView tvNumber;
        private TextView tvMode;
        private ImageView delete;
    }

    /**
     * 上一页
     *
     * @param view
     */
    public void prepPage(View view) {
        li_pd.setVisibility(View.VISIBLE);  //展示加载的圆圈
        if (mCurrentPage <= 0) {   //判断当前页是否是首页
            li_pd.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "已经是首页了", Toast.LENGTH_SHORT).show();
            return;
        }
        mCurrentPage--;
        initData();
    }

    /**
     * 下一页
     *
     * @param view
     */
    public void nextPage(View view) {

        li_pd.setVisibility(View.VISIBLE);  //展示加载的圆圈
        //判断当前的页码是否大于总的页数
        if (mCurrentPage >= totalPage - 1) {
            li_pd.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "已经是最后一页了", Toast.LENGTH_SHORT).show();
            return;
        }
        mCurrentPage++;
        initData();
    }

    /**
     * 调转
     *
     * @param view
     */
    public void jump(View view) {
        String etPage = etPageNumber.getText().toString().trim();
        if (TextUtils.isEmpty(etPage) || etPage.equals("0")) {     //在这里我们首先判断是否为空的时候用户点击的逻辑
            //如果用户在没有输入任何的状况下按下了按钮，就会提示以下说明
            Toast.makeText(this, "请输入你要查找的页数", Toast.LENGTH_SHORT).show();
            return;
        }
        int page = Integer.parseInt(etPage);
        mCurrentPage = --page;
        li_pd.setVisibility(View.VISIBLE);  //展示加载的圆圈
        if (mCurrentPage <= 0 && mCurrentPage > totalPage) {
            li_pd.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "没有你要查找的页数", Toast.LENGTH_SHORT).show();
            return;
        }
        initData();
        //当用户点击查询按钮之后，清空输入框的内容
        etPageNumber.setText("");
    }
}
