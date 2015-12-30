package com.example.mobilesafe.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by 若兰 on 2015/12/30.
 * 一个懂得了编程乐趣的小白，希望自己
 * 能够在这个道路上走的很远，也希望自己学习到的
 * 知识可以帮助更多的人,分享就是学习的一种乐趣
 * QQ:1069584784
 * csdn:http://blog.csdn.net/wuyinlei
 *
 * 短信备份的工具类
 */

public class SMSUtils {

    public static boolean backUpSms(Context context){

        /**
         * 目的：备份短信
         * 1、判断当前用户手机是不是有SD卡（没有就不用进行下一步了）
         *
         * 2、权限----（表示我们没有权限去读取内容）
         *      可以使用内容观察者来读取
         *
         * 3、写短信（写到SD卡）
         *
         */

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){


            //如果能进来，就说明用户有SD卡
            ContentResolver resolver = context.getContentResolver();

            //获取短信的路径
            Uri uri = Uri.parse("content://sms/");

            /**
             * address = 短信号码
             * data = 发送短信的时间
             * type = 1 接收短信
             * type = 2 发送短信
             * body = 短信内容
             * cursor是一个游标，在这里指向我们的数据
             */

            Cursor cursor = resolver.query(uri, new String[]{"address", "date", "type", "body"},
                    null, null, null);


            //写文件
            try {
                //把短信备份到SD卡   第二个参数表示名字
                File file = new File(Environment.getExternalStorageDirectory(),"backup.xml");

                FileOutputStream os = new FileOutputStream(file);

                //得到序列化器
                //在Android系统中，所有有关xml的解析都是pull解析
                XmlSerializer serializer = Xml.newSerializer();
                //把短信序列化到SD卡，然后设置编码格式
                serializer.setOutput(os,"utf-8");

                //第二个参数表示的是当前的xml是否是独立的文件  true表示独立
                //开始
                serializer.startDocument("utf-8",true);

                //设置开始的节点，第一个参数是命名空间，第二个参数是节点的名字
                serializer.startTag(null, "smss");


                /**
                 * 如果可以moveToNext（）就表明后面有内容
                 */
                while (cursor.moveToNext()) {
                   /* Log.d("SMSUtils", "------------------------------");
                    Log.d("SMSUtils", "address :" + cursor.getString(0));
                    Log.d("SMSUtils", "date : " + cursor.getString(1));
                    Log.d("SMSUtils", "type : " + cursor.getString(2));
                    Log.d("SMSUtils", "body : " + cursor.getString(3));*/
                    serializer.startTag(null, "sms");

                    serializer.startTag(null, "address");
                    //设置文本的内容
                    serializer.text(cursor.getString(0));
                    serializer.endTag(null, "address");


                    serializer.startTag(null, "date");
                    //设置文本的内容
                    serializer.text(cursor.getString(1));
                    serializer.endTag(null, "date");

                    serializer.startTag(null, "type");
                    //设置文本的内容
                    serializer.text(cursor.getString(2));
                    serializer.endTag(null,"type");

                    serializer.startTag(null,"body");
                    //设置文本的内容
                    serializer.text(cursor.getString(3 ));
                    serializer.endTag(null, "body");


                    serializer.endTag(null,"sms");

                    Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
                }


                //设置结束的节点，第一个参数是命名空间，第二个参数是节点的名字
                serializer.endTag(null,"smss");


                //结束
                serializer.endDocument();


            } catch (Exception e) {
                e.printStackTrace();
            }



        }


        return false;
    }

}
