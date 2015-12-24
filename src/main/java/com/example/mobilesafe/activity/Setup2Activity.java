package com.example.mobilesafe.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.mobilesafe.R;

public class Setup2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_2);
    }

    public void next(View view) {
        startActivity(new Intent(Setup2Activity.this, Setup3Activity.class));
    }

    public void previous(View view) {
        startActivity(new Intent(Setup2Activity.this,Setup1Activity.class));
    }
}
