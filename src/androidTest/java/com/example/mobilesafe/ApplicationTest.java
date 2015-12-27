package com.example.mobilesafe;

import android.app.Application;
import android.content.Context;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.example.mobilesafe.bean.BlackNumberInfo;
import com.example.mobilesafe.db.BlackNumberDao;

import java.util.List;
import java.util.Random;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    BlackNumberDao dao ;
    public Context context;
    @Override
    protected void setUp() throws Exception {
        this.context = getContext();
        super.setUp();
        dao = new BlackNumberDao(context);
    }

    public void testAdd(){

        for (int i = 0; i < 200; i++) {
            long number = 1332525415 + i;

            Random random = new Random();
            dao.insert(number + "",String.valueOf(random.nextInt(3) + 1));
        }
    }

    public void testDelete(){
        boolean delete = dao.delete(1332525415 + "");
        //断言的方式，通过比较期望值和所得值是否是一样的来判断程序是否运行正确
        assertEquals(true,delete);
    }

    public void testFind(){
        String number = dao.findNumber(1332525415 + "5");
    }
    
    public void testFindAll(){
        List<BlackNumberInfo> daoAll = dao.findAll();
        for (BlackNumberInfo info:daoAll) {
            Log.d("ApplicationTest", info.getMode() + "" + info.getNumber() + "");
        }
    }
}