package com.example.mobilesafe.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

public class CallSafeActivity extends AppCompatActivity {

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


    private void initialize() {

        li_pd = (LinearLayout) findViewById(R.id.li_pd);
        li_pd.setVisibility(View.VISIBLE);  //展示加载的圆圈
        listView = (ListView) findViewById(R.id.listView);
        etPageNumber = (EditText) findViewById(R.id.etPageNumber);
        tvPageNumber = (TextView) findViewById(R.id.tvPageNumber);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            totalPage = dao.getTotal() / pageSize;
            String size = (mCurrentPage + 1) + "/" + totalPage;
            tvPageNumber.setText(size);   //通过总的记录数/每一页的数据
            li_pd.setVisibility(View.INVISIBLE);
            adapter = new CallSafeAdapter(lists, CallSafeActivity.this);
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
                lists = dao.findPage(mCurrentPage, pageSize);
                mHandler.sendEmptyMessage(0);
            }
        }.start();

    }

    private class CallSafeAdapter extends MyBaseAdapter<BlackNumberInfo> {
        public CallSafeAdapter(List lists, Context mContext) {
            super(lists, mContext);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(CallSafeActivity.this, R.layout.item_call_safe, null);
                holder = new ViewHolder();
                holder.tvNumber = (TextView) convertView.findViewById(R.id.tvNumber);
                holder.tvMode = (TextView) convertView.findViewById(R.id.tvMode);
                holder.delete = (ImageView) convertView.findViewById(R.id.delete);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvNumber.setText(lists.get(position).getNumber());
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
        if (mCurrentPage <= 0) {
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
        //判断当前的页码不能大于总得页数
        if (mCurrentPage >= totalPage -1) {
            li_pd.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "已经是最后一页了", Toast.LENGTH_SHORT).show();
            return;
        }
        mCurrentPage ++ ;
        initData();
    }

    /**
     * 调转
     *
     * @param view
     */
    public void jump(View view) {
        String etPage = etPageNumber.getText().toString().trim();
        if (TextUtils.isEmpty(etPage) || etPage.equals("0")) {
            Toast.makeText(this, "请输入你要查找的页数", Toast.LENGTH_SHORT).show();
            return;
        }
        int page = Integer.parseInt(etPage);
        mCurrentPage = --page;
        li_pd.setVisibility(View.VISIBLE);  //展示加载的圆圈
        if (mCurrentPage <=0 && mCurrentPage >totalPage){
            li_pd.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "没有你要查找的页数", Toast.LENGTH_SHORT).show();
            return;
        }
        initData();
        etPageNumber.setText("");
    }
}
