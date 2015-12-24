package com.example.mobilesafe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.mobilesafe.R;

public class Setup4Activity extends BaseSetupActivity {

    private SharedPreferences mPres;
    private CheckBox checkbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_4);
        initialize();
        mPres = getSharedPreferences("config", MODE_PRIVATE);
        boolean protect = mPres.getBoolean("protect", false);
        if (protect) {
            checkbox.setText("防盗保护已经开启");
            checkbox.setChecked(true);
        } else {
            checkbox.setText("防盗保护没有开启");
            checkbox.setChecked(false);
        }
    }

    @Override
    public void showPreviousPage() {
        startActivity(new Intent(Setup4Activity.this, Setup3Activity.class));
        finish();
        overridePendingTransition(R.anim.previous_in, R.anim.previous_out);
    }

    @Override
    public void showNextPage() {
        startActivity(new Intent(Setup4Activity.this, LostFoubdActivity.class));
        finish();
        overridePendingTransition(R.anim.tran_out, R.anim.tran_in);
        //更新sp，表示已经展示过向导了，下次进来就不展示了
        mPres.edit().putBoolean("configed", true).commit();
    }


    private void initialize() {
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkbox.setText("防盗保护已经开启");
                    mPres.edit().putBoolean("protect", true).commit();
                } else {
                    checkbox.setText("防盗保护没有开启");
                    mPres.edit().putBoolean("protect", false).commit();
                }
            }
        });
    }
}
