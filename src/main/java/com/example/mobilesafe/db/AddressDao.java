package com.example.mobilesafe.db;

/**
 * Created by 若兰 on 2015/12/25.
 * 一个懂得了编程乐趣的小白，希望自己
 * 能够在这个道路上走的很远，也希望自己学习到的
 * 知识可以帮助更多的人,分享就是学习的一种乐趣
 * QQ:1069584784
 * csdn:http://blog.csdn.net/wuyinlei
 */

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

/**
 * 归属地查询数据库
 */
public class AddressDao {

    //该路径必须是一下的这个目录
    public static final String PATH = "data/data/com.example.mobilesafe/files/address.db";

    public static String getAddress(String number) {
        String address = "未知号码";
        //拿到数据库对象
        /**
         * openDatabase()这个api只支持从他默认的路径下打开数据库文件，不支持直接打开assets目录下的文件
         * 所以我们通过流的复制，把这个assets目录下的文件复制到了"data/data/com.example.mobilesafe/files"
         * 这个目录下，以达到我们可以读取数据库文件
         */
        SQLiteDatabase db = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);

        //分析输入的是否是手机号码，也就是用到了正则表达式
        //手机号码特点 1(345678) (9位数字)
        //手机号码的正则表达式
        //^1[3-8]\d{9}$
        if (number.matches("^1[3-8]\\d{9}$")) {   //匹配手机号码
            String sql = "select location from data2 where id=(select outkey from data1 where id=?)";
            Cursor cursor = db.rawQuery(sql, new String[]{number.substring(0, 7)});
            if (cursor.moveToNext()) {
                address = cursor.getString(0);
            }
            cursor.close();
        } else if (number.matches("^\\d+$")) {   //匹配数字
            switch (number.length()) {
                case 3:
                    address = "报警电话";
                    break;
                case 4:
                    address = "模拟器";
                    break;
                case 5:
                    address = "客服电话";
                    break;
                //判断本地电话
                case 7:
                case 8:

                    address = "本地电话";
                    break;

                //0102225125
                default:
                    if (number.startsWith("0") && number.length() > 10) { //有可能是长途电话
                        //有些区号是4位，有些区号是3位(包括0)
                        // 先查询4位区号
                        Cursor cursor = db.rawQuery(
                                "select location from data2 where area =?",
                                new String[]{number.substring(1, 4)});

                        if (cursor.moveToNext()) {
                            address = cursor.getString(0);
                        } else {
                            cursor.close();

                            // 查询3位区号
                            cursor = db.rawQuery(
                                    "select location from data2 where area =?",
                                    new String[]{number.substring(1, 3)});

                            if (cursor.moveToNext()) {
                                address = cursor.getString(0);
                            }

                            cursor.close();
                        }
                    }
                    address = "";
                    break;
            }
        }
        //关闭数据库
        db.close();
        return address;
    }
}
