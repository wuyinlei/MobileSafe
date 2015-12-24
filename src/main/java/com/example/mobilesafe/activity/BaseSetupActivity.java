package com.example.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


/**
 * Created by 若兰 on 2015/12/24.
 * 一个懂得了编程乐趣的小白，希望自己
 * 能够在这个道路上走的很远，也希望自己学习到的
 * 知识可以帮助更多的人,分享就是学习的一种乐趣
 * QQ:1069584784
 * csdn:http://blog.csdn.net/wuyinlei
 */

/**
 * 设置引导页的基类
 * <p/>
 * 不需要在mainfest文件中注册
 */
public abstract class BaseSetupActivity extends AppCompatActivity {

    private GestureDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //手势识别器
        detector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {
                    /**
                     *
                     * @param e1   第一个按下的event
                     * @param e2   第二个按下的event
                     * @param velocityX   x轴速度
                     * @param velocityY  y轴速度
                     * @return
                     */
                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                        /**
                         * 判断纵向滑动速度是否过大，过大的话不允许切换界面
                         */
                        if (Math.abs((e2.getRawY() - e1.getRawY())) > 100) {
                            Toast.makeText(BaseSetupActivity.this, "不能这样滑动的哦", Toast.LENGTH_SHORT).show();
                            return true;
                        }

                        //判断滑动是否过慢
                        if (Math.abs(velocityX) < 100) {
                            Toast.makeText(BaseSetupActivity.this, "滑动的太慢了", Toast.LENGTH_SHORT).show();
                            return true;
                        }

                        //向右滑，上一页
                        if (e2.getRawX() - e1.getRawX() > 200) {
                            showPreviousPage();
                            return true;
                        }
                        //向左滑，下一页
                        else if (e1.getRawX() - e2.getRawX() > 200) {
                            showNextPage();
                            return true;
                        }
                        return super.onFling(e1, e2, velocityX, velocityY);
                    }
                });
    }

    /**
     * 展示上一页
     */
    public abstract void showPreviousPage() ;
    /**
     * 展示下一页
     */
    public abstract void showNextPage();

    /**
     * 下一个页面
     *
     * @param view
     */
    public void next(View view) {
        showNextPage();
    }

    /**
     * 上一个页面
     *
     * @param view
     */
    public void previous(View view) {
        showPreviousPage();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);  //委托手势识别器处理
        return super.onTouchEvent(event);

    }
}
