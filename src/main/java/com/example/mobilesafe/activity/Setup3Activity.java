package com.example.mobilesafe.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.mobilesafe.R;

public class Setup3Activity extends BaseSetupActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_3);
    }

    @Override
    public void showPreviousPage() {
        startActivity(new Intent(Setup3Activity.this, Setup2Activity.class));
        finish();
        overridePendingTransition(R.anim.previous_in, R.anim.previous_out);
    }

    @Override
    public void showNextPage() {
        startActivity(new Intent(Setup3Activity.this, Setup4Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_out, R.anim.tran_in);
    }

}
