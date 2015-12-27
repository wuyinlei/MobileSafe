package com.example.mobilesafe.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
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

    //一共有多少页
    private int totalPage;


    /**
     * 开始的位置
     */
    private int startIndex = 0;

    /**
     * 每页展示的最大的数量
     */
    private int maxCount = 20;
    private EditText etphone;
    private CheckBox cbphone;
    private CheckBox cbsms;
    private Button btnSure;
    private Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_safe);
        lists = new ArrayList<>();
        dao = new BlackNumberDao(this);
        initialize();
        initData();
        initialize();
    }


    /**
     * 初始化布局控件
     */
    private void initialize() {

        li_pd = (LinearLayout) findViewById(R.id.li_pd);
        li_pd.setVisibility(View.VISIBLE);  //展示加载的圆圈
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            /**
             * 状态改变的时候调用的方法
             * @param view
             * @param scrollState   滚动的状态
             *
             *                      AbsListView.OnScrollListener.SCROLL_STATE_IDLE  闲置的状态
             *                      AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL  手指触摸的时候的状态
             *                      AbsListView.OnScrollListener.SCROLL_STATE_FLING  惯性
             */
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        //获取到最后一条可见的数据
                        int lastVisiblePosition = listView.getLastVisiblePosition();
                        if (lastVisiblePosition == lists.size() - 1) {
                            startIndex += maxCount;
                            if (lastVisiblePosition > totalPage) {
                                Toast.makeText(CallSafeActivity.this, "已经没有数据了", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            initData();
                        }

                        break;
                  /*  case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        break;*/
                }
            }

            /**
             * 滚动的时候调用的方法
             * @param view
             * @param firstVisibleItem
             * @param visibleItemCount
             * @param totalItemCount
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    /**
     * 添加黑名单
     *
     * @param view
     */
    public void addBlackNumber(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = View.inflate(this, R.layout.add_black_number, null);
        etphone = (EditText) v.findViewById(R.id.et_phone);
        cbphone = (CheckBox) v.findViewById(R.id.cb_phone);
        cbsms = (CheckBox) v.findViewById(R.id.cb_sms);
        btnSure = (Button) v.findViewById(R.id.btnSure);
        final AlertDialog dialog = builder.create();
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = etphone.getText().toString().trim();
                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(CallSafeActivity.this, "请输入黑名单号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                String mode = "";
                if (cbphone.isChecked() && cbsms.isChecked()) {
                    mode = "1";
                } else if (cbphone.isChecked()) {
                    mode = "2";
                } else if (cbsms.isChecked()) {
                    mode = "3";
                } else {
                    Toast.makeText(CallSafeActivity.this, "请选择拦截模式", Toast.LENGTH_SHORT).show();
                    return;
                }
                BlackNumberInfo info = new BlackNumberInfo();
                info.setNumber(phoneNumber);
                info.setMode(mode);

                //添加的时候把数据添加到第一条，这样方便我们查看是否添加成功，也可以用其他的方式来判断是否添加成功
                lists.add(0,info);

                //把电话号码和拦截模式添加到数据库
                dao.insert(phoneNumber, mode);

                //如果不做下面的判断的话，那么就会每次在更新加载数据的时候，显示的都是第一条数据
                if(adapter == null){
                    adapter = new CallSafeAdapter(lists, CallSafeActivity.this);
                    listView.setAdapter(adapter);
                }else{
                    //在这如果有adapter不为空，那么直接改变位置
                    adapter.notifyDataSetChanged();
                }
                dialog.dismiss();
            }
        });

        btnCancel = (Button) v.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setView(v);
        dialog.show();
    }


    /**
     * 通过Handler异步通信，新开启子线程来查找数据（工作中的数据是很多的，如果在UI线程中更新的话会导致性能下降的）
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            //得到总得页数
            li_pd.setVisibility(View.INVISIBLE);   //设置进度条不可见
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
                //一共有多少条数据
                totalPage = dao.getTotal();
                Log.d("CallSafeActivity", "totalPage" + totalPage);
                //在这里通过findPage传入当前页数和每页的数量的多少来获得数据

                /**
                 * 如果在滑动的时候仅仅有了下面一句，是达不到效果的，这个时候只会展示一页的数据
                 * 其他的都是空白页
                 */
                //lists = dao.findPage2(startIndex, maxCount);
                if (lists == null) {
                    lists = dao.findPage2(startIndex, maxCount);
                } else {
                    //这样写为了防止新加的20条数据把之前的给覆盖了，达到的效果是
                    //当增加了数据的时候，之前的数据还是可见的
                    lists.addAll(dao.findPage2(startIndex, maxCount));
                }
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
                convertView = View.inflate(CallSafeActivity.this, R.layout.item_call_safe, null);
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


}
