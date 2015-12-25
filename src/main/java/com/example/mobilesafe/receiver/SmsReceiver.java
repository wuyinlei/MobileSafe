package com.example.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import com.example.mobilesafe.R;
import com.example.mobilesafe.service.LocationService;

/**
 * Created by 若兰 on 2015/12/24.
 * 一个懂得了编程乐趣的小白，希望自己
 * 能够在这个道路上走的很远，也希望自己学习到的
 * 知识可以帮助更多的人,分享就是学习的一种乐趣
 * QQ:1069584784
 * csdn:http://blog.csdn.net/wuyinlei
 */

/**
 * 拦截短信
 */
public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
        for (Object object : pdus) {    //短信最多是140字节，超出的话会多条短信发出
            SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
            String messageAddress = message.getOriginatingAddress();  //短信的来源号码
            String messageBody = message.getMessageBody();  //短信内容

            //播放报警音乐
            if ("#*alarm*#".equals(messageBody)) {
                MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);

                //声音调为静音也是额可以的
                player.setVolume(1f, 1f);   //把声音调整最大值
                player.setLooping(true);  //循环播放
                player.start();   //开始播放
                abortBroadcast();   //中断短信的传通，从而系统就不能收到短信的内容了
            }
            //发送地理坐标
            else if ("#*location*#".equals(messageBody)) {
                //获取经纬度坐标
                context.startService(new Intent(context, LocationService.class));
                SharedPreferences mPres = context.getSharedPreferences("config", Context.MODE_PRIVATE);
                String location = mPres.getString("location", "getting location。。。。");

                abortBroadcast();   //中断短信的传通，从而系统就不能收到短信的内容了
            }
            //清除所有数据
            else if ("#*wipedata*#".equals(messageBody)) {

                abortBroadcast();   //中断短信的传通，从而系统就不能收到短信的内容了
            }
            //锁屏
            else if ("#*lockscreen*#".equals(messageBody)) {

                abortBroadcast();   //中断短信的传通，从而系统就不能收到短信的内容了

            }
        }
    }
}
