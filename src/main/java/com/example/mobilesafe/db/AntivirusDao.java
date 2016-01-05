package com.example.mobilesafe.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by 若兰 on 2016/1/5.
 * 一个懂得了编程乐趣的小白，希望自己
 * 能够在这个道路上走的很远，也希望自己学习到的
 * 知识可以帮助更多的人,分享就是学习的一种乐趣
 * QQ:1069584784
 * csdn:http://blog.csdn.net/wuyinlei
 */

public class AntivirusDao {

    //该路径必须是一下的这个目录
    public static final String PATH = "data/data/com.example.mobilesafe/files/antivirus.db";

    /**
     * 检查当前的md5值是否在数据库中
     *
     * @param md5
     * @return
     */
    public static String checkFileVirus(String md5) {

        String desc = null;
        //拿到数据库对象
        /**
         * openDatabase()这个api只支持从他默认的路径下打开数据库文件，不支持直接打开assets目录下的文件
         * 所以我们通过流的复制，把这个assets目录下的文件复制到了"data/data/com.example.mobilesafe/files"
         * 这个目录下，以达到我们可以读取数据库文件
         */
        SQLiteDatabase db = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);

        String sql = "select desc from datable where md5 = ?";

        //查询当前的md5是否在数据库中
        Cursor cursor = db.rawQuery(sql, new String[]{md5});

        //判断当前的游标是否可以移动
        if (cursor.moveToNext()) {
            //如果可以移动，就代表有病毒，然后把详细信息得到
            desc = cursor.getString(0);
        }
        return desc;
    }
}
