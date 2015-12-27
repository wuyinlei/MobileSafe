package com.example.mobilesafe.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

import com.example.mobilesafe.bean.BlackNumberInfo;

import java.security.cert.CertificateParsingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 若兰 on 2015/12/27.
 * 一个懂得了编程乐趣的小白，希望自己
 * 能够在这个道路上走的很远，也希望自己学习到的
 * 知识可以帮助更多的人,分享就是学习的一种乐趣
 * QQ:1069584784
 * csdn:http://blog.csdn.net/wuyinlei
 */

public class BlackNumberDao {

    private final BlackNumberOpenHelper dbHelper;
    private SQLiteDatabase db;
    private static final String TABLE_NAME = "blacknumber";

    public BlackNumberDao(Context context) {
        dbHelper = new BlackNumberOpenHelper(context);

    }

    /**
     * @param number 黑名单号码
     * @param mode   拦截模式
     */
    public boolean insert(String number, String mode) {


        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number", number);
        values.put("mode", mode);
        long rowid = db.insert(TABLE_NAME, number, values);
        if (rowid == -1) {
            return false;
        }
        return true;
    }

    /**
     * 通过电话号码删除
     *
     * @param number
     */
    public boolean delete(String number) {
        db = dbHelper.getWritableDatabase();
        int rownumber = db.delete(TABLE_NAME, "number=?", new String[]{number});
        if (rownumber == 0) {
            return false;
        }
        return true;
    }

    /**
     * 通过电话号码修改
     */
    public boolean change(String number, String mode) {

        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mode", mode);
        int rowid = db.update(TABLE_NAME, values, "number=?", new String[]{number});
        if (rowid == 0) {
            return false;
        }
        return true;
    }


    /**
     * 通过电话号码查找
     *
     * @return
     */
    public String findNumber(String number) {
        db = dbHelper.getWritableDatabase();
        String mode = "";
        Cursor cursor = db.query(TABLE_NAME, new String[]{"mode"}, "number = ?", new String[]{number}, null, null, null, null);
        if (cursor.moveToNext()) {
            mode = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return mode;
    }

    /**
     * 查询所有的黑名单
     *
     * @return
     */
    public List<BlackNumberInfo> findAll() {

        db = dbHelper.getWritableDatabase();
        ArrayList<BlackNumberInfo> numberInfo = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME, new String[]{"number", "mode"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            String number = cursor.getString(0);
            String mode = cursor.getString(1);
            blackNumberInfo.setNumber(number);
            blackNumberInfo.setMode(mode);
            numberInfo.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
       /* try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/   //上面的这个是进行睡眠3秒，下面的源码里面已经实现了这个，所以用下面的更加方便
        //睡眠3秒

        SystemClock.sleep(3000);
        return numberInfo;
    }

    /**
     * 分页加载数据
     *
     * @param pageNumber 表示当前哪一页
     * @param pageSize   辨识每一页有多少条数据
     * @return
     */
    public List<BlackNumberInfo> findPage(int pageNumber, int pageSize) {

        db = dbHelper.getWritableDatabase();
        //limit   限制的意思  限制当前有多少数据   offset  是跳过的意思  从第几条开始
        String sql = "select number,mode from " + TABLE_NAME + " limit ? offset ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(pageSize),
                String.valueOf(pageSize * pageNumber)});
        ArrayList<BlackNumberInfo> infos = new ArrayList<>();
        while (cursor.moveToNext()) {
            BlackNumberInfo info = new BlackNumberInfo();
            info.setNumber(cursor.getString(0));
            info.setMode(cursor.getString(1));
            infos.add(info);
        }
        cursor.close();
        db.close();
        SystemClock.sleep(3000);
        return infos;
    }

    /**
     * //通过总的记录数/每一页的数据
     */
    public int getTotal() {
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from " + TABLE_NAME, null);
        cursor.moveToNext();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }
}
