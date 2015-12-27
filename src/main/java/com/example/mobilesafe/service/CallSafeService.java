package com.example.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.SmsMessage;

import com.example.mobilesafe.db.BlackNumberDao;

public class CallSafeService extends Service {

    private InnerReceive receive;
    private BlackNumberDao dao;


    public CallSafeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        receive = new InnerReceive();

        dao = new BlackNumberDao(this);
        //注册了短信的广播，初始化
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(receive, filter);
    }

    private class InnerReceive extends BroadcastReceiver {

        //在这里就可以接受短信了
        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            for (Object object : pdus) {    //短信最多是140字节，超出的话会多条短信发出
                SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
                String messageAddress = message.getOriginatingAddress();  //短信的来源号码
                String messageBody = message.getMessageBody();  //短信内容

                /**
                 * 拦截模式
                 * mode
                 * 1：全部拦截
                 * 2：电话拦截
                 * 3：短信拦截
                 */
                //查到拦截的模式
                String mode = dao.findNumber(messageAddress);
                if (mode.equals("1")) {
                    abortBroadcast();   //短信拦截
                } else if (mode.equals("3")) {
                    abortBroadcast();  //短信拦截
                }

                //电话拦截可以参考AddressService中的电话     或者这个http://bbs.51cto.com/thread-1027220-1.html


                //只能拦截模式   把只能拦截的数据放到数据库中，然后查看接受的短信数据和
                //数据库中的对比，如果有就 拦截 abortBroadcast();
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
