package com.example.mobilesafe.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mobilesafe.R;

public class Setup3Activity extends BaseSetupActivity {

    private EditText editPhone;
    private Button selectPhonenum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_3);
        initialize();
    }

    @Override
    public void showPreviousPage() {
        startActivity(new Intent(Setup3Activity.this, Setup2Activity.class));
        finish();
        overridePendingTransition(R.anim.previous_in, R.anim.previous_out);
    }

    @Override
    public void showNextPage() {
        String phone = editPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "安全号码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        mPres.edit().putString("safe_phone", phone).commit();  //保存安全号码
        startActivity(new Intent(Setup3Activity.this, Setup4Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_out, R.anim.tran_in);
    }

    private void initialize() {
        editPhone = (EditText) findViewById(R.id.editPhone);
        String safePhone = mPres.getString("safe_phone", "");
        editPhone.setText(safePhone);
        selectPhonenum = (Button) findViewById(R.id.selectPhonenum);
        selectPhonenum.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                //在这里就不要finish了，要不然这个界面这个界面销毁了，那个onActivityResult就没用了
                startActivityForResult(new Intent(Setup3Activity.this, ContactActivity.class), 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String phone = data.getStringExtra("phone");
            phone = phone.replaceAll("-", "").replaceAll(" ", "");  //替换空段和空格
            editPhone.setText(phone);   //把电话号码设置给输入框
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
