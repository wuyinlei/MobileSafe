package com.example.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by 若兰 on 2015/12/24.
 * 一个懂得了编程乐趣的小白，希望自己
 * 能够在这个道路上走的很远，也希望自己学习到的
 * 知识可以帮助更多的人,分享就是学习的一种乐趣
 * QQ:1069584784
 * csdn:http://blog.csdn.net/wuyinlei
 */

/**
 * 监听手机启动的广播
 */
public class BootCompleteReceiver extends BroadcastReceiver {

    private SharedPreferences mPres;

    @Override
    public void onReceive(Context context, Intent intent) {
        //获取绑定的sim卡
        mPres = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        String sim = mPres.getString("sim", null);
        if (!TextUtils.isEmpty(sim)){
            //获取最新的sim卡
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String currentSim = tm.getSimSerialNumber();
            if (sim.equals(currentSim)) {
                Log.d("BootCompleteReceiver", "手机安全");
            } else {
                Log.d("BootCompleteReceiver", "手机危险,发送报警短信");
                String safePhone = mPres.getString("safe_phone", "");

                //发送短信的逻辑
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(safePhone,null,"sim cart changed",null,null);
            }
        }
    }
}
