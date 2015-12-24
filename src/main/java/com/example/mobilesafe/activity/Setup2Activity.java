package com.example.mobilesafe.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.example.mobilesafe.R;

public class Setup2Activity extends BaseSetupActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_2);
    }

    /**
     * 展示上一页
     */
    public void showPreviousPage(){
        startActivity(new Intent(Setup2Activity.this, Setup1Activity.class));
        finish();
        overridePendingTransition(R.anim.previous_in, R.anim.previous_out);
    }

    /**
     * 展示下一页
     */
    public void showNextPage(){
        startActivity(new Intent(Setup2Activity.this, Setup3Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_out, R.anim.tran_in);
    }

}
