package com.example.mobilesafe.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by 若兰 on 2016/1/5.
 * 一个懂得了编程乐趣的小白，希望自己
 * 能够在这个道路上走的很远，也希望自己学习到的
 * 知识可以帮助更多的人,分享就是学习的一种乐趣
 * QQ:1069584784
 * csdn:http://blog.csdn.net/wuyinlei
 */

public class AppLockDao {

    private AppLockOpenHelper helper;
    private SQLiteDatabase db;

    public AppLockDao(Context context) {
        helper = new AppLockOpenHelper(context);

    }

    /**
     * 添加到程序锁里面
     *
     * @param packageName 包名
     */
    public void addApp(String packageName) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("packagename", packageName);

        db.insert("info", null, values);
        db.close();
    }


    /**
     * 从程序锁里面删除
     *
     * @param packageName
     */
    public void delete(String packageName) {
        db = helper.getWritableDatabase();
        db.delete("info", "packagename=?", new String[]{packageName});
        db.close();
    }

    /**
     * 查询当前的包是否在锁里面
     *
     * @param packageName
     * @return
     */
    public boolean find(String packageName) {
        boolean result = false;
        db = helper.getReadableDatabase();
        Cursor cursor = db.query("info", null, "packagename=?", new String[]{packageName}, null, null, null);
        if (cursor.moveToNext()) {
            result = true;
        }
        cursor.close();
        db.close();
        return result;

    }

    /**
     * 查询全部的锁定的包名
     * @return
     */
    public List<String> findAll(){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("info", new String[]{"packagename"}, null, null, null, null, null);
        List<String> packnames = new ArrayList<String>();
        while(cursor.moveToNext()){
            packnames.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return packnames;
    }
}
