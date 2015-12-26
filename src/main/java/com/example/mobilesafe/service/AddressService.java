package com.example.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafe.R;
import com.example.mobilesafe.db.AddressDao;

/**
 * Created by 若兰 on 2015/12/25.
 * 一个懂得了编程乐趣的小白，希望自己
 * 能够在这个道路上走的很远，也希望自己学习到的
 * 知识可以帮助更多的人,分享就是学习的一种乐趣
 * QQ:1069584784
 * csdn:http://blog.csdn.net/wuyinlei
 */

/**
 * 来电提醒的服务
 */
public class AddressService extends Service {

    TelephonyManager tm;
    MyListener listener;
    WindowManager mWM;
    View view;
    OutCallReceiver receiver;

    private SharedPreferences mPres;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPres = getSharedPreferences("config", MODE_PRIVATE);
        //获取手机电话的服务
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new MyListener();
        //监听打电话的状态
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        receiver = new OutCallReceiver();

        //动态的注册广播
        IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(receiver, filter);
    }

    class MyListener extends PhoneStateListener {

        //拨打电话的时候
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:  //电话铃响了
                    String address = AddressDao.getAddress(incomingNumber); //根据来电号码查询归属地
                    showToast(address);
                    // Toast.makeText(AddressService.this, address, Toast.LENGTH_LONG).show();
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    //电话闲置状态
                    if (mWM != null && view != null) {
                        mWM.removeView(view);   //从windoe中移除view
                    }
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    /**
     * 在这里我们可以动态的注册广播，同服务同生死
     */
    class OutCallReceiver extends BroadcastReceiver {

        /***
         * 需要这个<uses-permission android:name="android.permission.PROCESS_OUTGOING_CALLS"/>
         * 监听去电的广播接受者
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            String number = getResultData();   //获取去电的电话号码

            String address = AddressDao.getAddress(number);
            showToast(address);
            //Toast.makeText(context, address, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //停止来电监听
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
        unregisterReceiver(receiver);
    }

    /**
     * 自定义归属地悬浮框，在Window级别上显示，就是在主屏中显示
     * 这个是我们根据toast的源码改变的，（因为toast可以在任何地方显示）
     */
    private void showToast(String text) {
        Toast.makeText(this, "你好", Toast.LENGTH_SHORT).show();
        //可以在第三方app中弹出悬浮框   例如360的悬浮框
        //初始化WindowManager
        mWM = (WindowManager) this.getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.setTitle("Toast");

        int style = mPres.getInt("address_style", 0);  //读取保存的style

        //归属地背景的数组
        int[] bgs = new int[]{R.drawable.call_locate_white1, R.drawable.call_locate_orange1
                , R.drawable.call_locate_blue1, R.drawable.call_locate_gray1, R.drawable.call_locate_green1};
        //加载自定义的布局
        view = View.inflate(this, R.layout.toast_text_address, null);

        //在这儿设置背景，根据存储的style样式更新背景
        view.setBackgroundResource(bgs[style]);
        TextView tvText = (TextView) view.findViewById(R.id.tvNumber);
        //下面就把自己定义的toast显示在了window上
        tvText.setText(text);
        mWM.addView(view, params);

    }
}
