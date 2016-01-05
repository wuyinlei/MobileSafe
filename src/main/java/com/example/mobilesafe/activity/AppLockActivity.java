package com.example.mobilesafe.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.mobilesafe.R;
import com.example.mobilesafe.fragment.LockingFragment;
import com.example.mobilesafe.fragment.UnLockFragment;

/**
 * 加锁
 */
public class AppLockActivity extends FragmentActivity implements View.OnClickListener {


    private TextView unLock;
    private TextView locking;
    private FrameLayout frcontent;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private UnLockFragment unLockFragment;
    private LockingFragment lockingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        initialize();
    }


    /**
     * 初始化布局控件
     */
    private void initialize() {

        unLock = (TextView) findViewById(R.id.unLock);
        unLock.setOnClickListener(this);
        locking = (TextView) findViewById(R.id.locking);
        locking.setOnClickListener(this);
        frcontent = (FrameLayout) findViewById(R.id.fr_content);

        //得到fragment的管理者
        fragmentManager = getSupportFragmentManager();

        //开启事务
        transaction = fragmentManager.beginTransaction();

        unLockFragment = new UnLockFragment();
        lockingFragment = new LockingFragment();

        /**
         * 替换界面
         * 第一个参数    需要替换的id
         * 第二个参数    用那个fragment来替换
         *
         * 然后要提交
         */
        transaction.replace(R.id.fr_content,unLockFragment).commit();
    }

    @Override
    public void onClick(View v) {

        FragmentTransaction ft = fragmentManager.beginTransaction();
        switch (v.getId()){
            case R.id.unLock:
                unLock.setBackgroundResource(R.mipmap.tab_left_pressed);
                locking.setBackgroundResource(R.mipmap.tab_right_default);
                ft.replace(R.id.fr_content, unLockFragment);
                //Log.d("AppLockActivity", "切换到加锁");
                break;
            case R.id.locking:
                unLock.setBackgroundResource(R.mipmap.tab_left_default);
                locking.setBackgroundResource(R.mipmap.tab_right_pressed);
                ft.replace(R.id.fr_content, lockingFragment);
                //Log.d("AppLockActivity", "切换到解锁");
                break;
        }
        ft.commit();
    }
}
