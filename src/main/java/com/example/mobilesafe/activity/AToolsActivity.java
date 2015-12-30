package com.example.mobilesafe.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafe.R;
import com.example.mobilesafe.utils.SMSUtils;

/**
 * 高级工具
 */
public class AToolsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
    }


    /**
     * 电话号码查询
     *
     * @param view
     */
    public void numberAddressQuery(View view) {
        Toast.makeText(this, "点击了我", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(AToolsActivity.this, AddressActivity.class));
    }

    /**
     * 短信备份功能
     * @param view
     */
    public void backUpSms(View view) {
        boolean result = SMSUtils.backUpSms(this);
    }
}
