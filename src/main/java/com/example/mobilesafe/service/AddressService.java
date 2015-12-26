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
import android.view.Gravity;
import android.view.MotionEvent;
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
    WindowManager.LayoutParams params;

    private int startX, startY;

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
     * 需要以下的权限
     * <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
     * 自定义归属地悬浮框，在Window级别上显示，就是在主屏中显示
     * 这个是我们根据toast的源码改变的，（因为toast可以在任何地方显示）
     */
    private void showToast(String text) {
        Toast.makeText(this, "你好", Toast.LENGTH_SHORT).show();
        //可以在第三方app中弹出悬浮框   例如360的悬浮框
        //初始化WindowManager
        mWM = (WindowManager) this.getSystemService(WINDOW_SERVICE);

        //获取屏幕的宽高
        final int width = mWM.getDefaultDisplay().getWidth();
        final int height = mWM.getDefaultDisplay().getHeight();


        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                // | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE   这个要去掉，否则不能响应我们的触摸事件
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        //电话窗口，用于电话交互
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.gravity = Gravity.LEFT + Gravity.TOP;   //将重心位置设置到左上方(0,0)
        //而不是默认的重心位置
        params.setTitle("Toast");

        int style = mPres.getInt("address_style", 0);  //读取保存的style

        //获取到保存的坐标，基于左上方的偏移量
        int lastX = mPres.getInt("lastX", 0);
        int lastY = mPres.getInt("lastY", 0);

        //设置悬浮窗的位置
        params.x = lastX;
        params.y = lastY;
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

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //获取初始坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        //获取移动后的坐标
                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();

                        //计算偏移量
                        int dx = endX - startX;
                        int dy = endY - startY;

                        //更新悬浮窗位置
                        params.x += startX;
                        params.y += startY;

                        /**
                         * 以下四个if判断是为了防止坐标偏移出屏幕
                         * 原因解释，因为在window界面中，我们是可以随便移动的，就算屏幕没有
                         * 那么大，但是他的x轴的坐标或者是y轴的坐标会一直增加，这个时候
                         * 当我们进入我们的应用中的时候查找这个图标的时候是找不到的，因为已经
                         * 跳离开了我们的屏幕，于是我们有了以下的四个逻辑判断
                         */
                        //防止坐标偏移出屏幕
                        if (params.x < 0) {
                            params.x = 0;
                        }

                        //防止坐标偏移出屏幕
                        if (params.y < 0) {
                            params.y = 0;
                        }

                        //防止坐标偏移出屏幕
                        if (params.x > width - view.getWidth()) {
                            params.x = width - view.getWidth();
                        }

                        //防止坐标偏移出屏幕
                        if (params.y > height - view.getHeight()) {
                            params.y = height - view.getHeight();
                        }

                        mWM.updateViewLayout(view, params);

                        //重新初始化起点坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP:
                        //记录抬起后的坐标点，然后我们保存并且记录，来让下一次重新进入的时候更新位置
                        SharedPreferences.Editor edit = mPres.edit();
                        edit.putInt("lastX", params.x);
                        edit.putInt("lastY", params.y);
                        edit.commit();
                        break;
                }

                return true;
            }
        });

    }
}
