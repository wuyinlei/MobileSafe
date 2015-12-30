package com.example.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.example.mobilesafe.db.BlackNumberDao;

import java.lang.reflect.Method;

public class CallSafeService extends Service {

    private InnerReceive receive;
    private BlackNumberDao dao;
    private TelephonyManager tm;
    private MyPhoneStateListener listener;
    private Class<?> clazz;


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

        //获取到系统的电话服务
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        listener = new MyPhoneStateListener();
        tm.listen(listener,Integer.MAX_VALUE);

        //注册了短信的广播，初始化
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(receive, filter);
    }

    class MyPhoneStateListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state){
                case TelephonyManager.CALL_STATE_IDLE:
                    //电话空闲状态
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    //电话铃响

                    String mode = dao.findNumber(incomingNumber);
                    if (mode.equals("1") || mode.equals("2")){
                        //得到uri
                        Uri uri= Uri.parse("content://call_log/calls");
                        getContentResolver().registerContentObserver(uri,true,new MyContentObserver(new Handler(),incomingNumber));

                        //挂断电话
                        endCall();
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //电话接起状态
                    break;

            }
        }
    }

    private class MyContentObserver extends ContentObserver{

        String incomingNumber;

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler,String incomingNumber) {
            super(handler);
            this.incomingNumber = incomingNumber;
        }


        //当数据改变的额时候调用此方法
        @Override
        public void onChange(boolean selfChange) {
            
            getContentResolver().unregisterContentObserver(this);
            deleteCallNumber(incomingNumber);
            super.onChange(selfChange);
        }
    }

    /**
     * 挂断电话删除电话号码
     * @param incomingNumber
     */
    private void deleteCallNumber(String incomingNumber) {
        Uri uri= Uri.parse("content://call_log/calls");
        getContentResolver().delete(uri,"number=?",new String[]{incomingNumber});
    }


    /**
     * 挂断电话
     */
    private void endCall() {

        try {
            //通过类加载器加载ServiceManager
            clazz = getClassLoader().loadClass("android.os.ServiceManager");
            //通过反射得到当前的方法
            Method method = clazz.getDeclaredMethod("getService", String.class);

            IBinder iBinder = (IBinder) method.invoke(null, TELECOM_SERVICE);

            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);

            iTelephony.endCall();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检测短信的广播
     */
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
