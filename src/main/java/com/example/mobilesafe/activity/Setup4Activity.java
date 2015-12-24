package com.example.mobilesafe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.mobilesafe.R;

public class Setup4Activity extends AppCompatActivity {

    private SharedPreferences mPres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_4);
        mPres = getSharedPreferences("config", MODE_PRIVATE);
    }

    public void next(View view) {
        startActivity(new Intent(Setup4Activity.this, LostFoubdActivity.class));
        finish();
        //更新sp，表示已经展示过向导了，下次进来就不展示了
        mPres.edit().putBoolean("configed",true).commit();
    }

    public void previous(View view) {
        startActivity(new Intent(Setup4Activity.this, Setup3Activity.class));
    }
}
