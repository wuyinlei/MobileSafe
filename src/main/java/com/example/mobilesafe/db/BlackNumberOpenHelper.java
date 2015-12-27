package com.example.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 若兰 on 2015/12/27.
 * 一个懂得了编程乐趣的小白，希望自己
 * 能够在这个道路上走的很远，也希望自己学习到的
 * 知识可以帮助更多的人,分享就是学习的一种乐趣
 * QQ:1069584784
 * csdn:http://blog.csdn.net/wuyinlei
 */

public class BlackNumberOpenHelper extends SQLiteOpenHelper {

    public BlackNumberOpenHelper(Context context) {
        super(context, "safe.db", null, 1);
    }

    /**
     * 创建表 blacknumber
     * _id  主键自动增长
     * number  电话号码
     * mode   拦截模式   电话拦截  短信拦截    电话短信拦截
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //String sql = "create table blacknumber (_id integer primary key autoincrement,number varchar(20),mode varchar(2))"
        db.execSQL("create table blacknumber (_id integer primary key autoincrement, number varchar(20),mode varchar(2))");
    }

    /**
     * 更新表
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
