package com.example.mobilesafe.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mobilesafe.R;
import com.example.mobilesafe.utils.UIUtils;

import java.util.IllegalFormatCodePointException;

/**
 * 输入密码的界面
 */
public class EnterPwdActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etpwd;
    private Button btnok;
    private Button bt1;
    private Button bt2;
    private Button bt3;
    private Button bt4;
    private Button bt5;
    private Button bt6;
    private Button bt7;
    private Button bt8;
    private Button bt9;
    private Button btcleanall;
    private Button bt0;
    private Button btdelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pwd);
        initialize();
    }

    /**
     * 初始化布局控件
     */
    private void initialize() {

        etpwd = (EditText) findViewById(R.id.et_pwd);
        btnok = (Button) findViewById(R.id.btn_ok);
        btnok.setOnClickListener(this);
        bt1 = (Button) findViewById(R.id.bt_1);
        bt1.setOnClickListener(this);
        bt2 = (Button) findViewById(R.id.bt_2);
        bt2.setOnClickListener(this);
        bt3 = (Button) findViewById(R.id.bt_3);
        bt3.setOnClickListener(this);
        bt4 = (Button) findViewById(R.id.bt_4);
        bt4.setOnClickListener(this);
        bt5 = (Button) findViewById(R.id.bt_5);
        bt5.setOnClickListener(this);
        bt6 = (Button) findViewById(R.id.bt_6);
        bt6.setOnClickListener(this);
        bt7 = (Button) findViewById(R.id.bt_7);
        bt7.setOnClickListener(this);
        bt8 = (Button) findViewById(R.id.bt_8);
        bt8.setOnClickListener(this);
        bt9 = (Button) findViewById(R.id.bt_9);
        bt9.setOnClickListener(this);
        btcleanall = (Button) findViewById(R.id.bt_clean_all);
        btcleanall.setOnClickListener(this);
        bt0 = (Button) findViewById(R.id.bt_0);
        bt0.setOnClickListener(this);
        btdelete = (Button) findViewById(R.id.bt_delete);
        btdelete.setOnClickListener(this);

        //隐藏当前的键盘
        etpwd.setInputType(InputType.TYPE_NULL);
    }

    @Override
    public void onClick(View v) {

        String str = etpwd.getText().toString();
        switch (v.getId()) {
            case R.id.bt_0:
                etpwd.setText(str + bt0.getText().toString());
                break;
            case R.id.bt_1:
                etpwd.setText(str + bt1.getText().toString());
                break;
            case R.id.bt_2:
                etpwd.setText(str + bt2.getText().toString());
                break;
            case R.id.bt_3:
                etpwd.setText(str + bt3.getText().toString());
                break;
            case R.id.bt_4:
                etpwd.setText(str + bt4.getText().toString());
                break;
            case R.id.bt_5:
                etpwd.setText(str + bt5.getText().toString());
                break;
            case R.id.bt_6:
                etpwd.setText(str + bt6.getText().toString());
                break;
            case R.id.bt_7:
                etpwd.setText(str + bt7.getText().toString());
                break;
            case R.id.bt_8:
                etpwd.setText(str + bt8.getText().toString());
                break;
            case R.id.bt_9:
                etpwd.setText(str + bt9.getText().toString());
                break;
            case R.id.bt_delete:
                str = etpwd.getText().toString();
                if (str.length() == 0) {
                    return;
                }
                etpwd.setText(str.substring(0, str.length() - 1));
                break;
            case R.id.bt_clean_all:
                etpwd.setText("");
                break;
            case R.id.btn_ok:
                //获取到输入的密码
                String result = etpwd.getText().toString().trim();
                if ("123".equals(result)){

                } else {
                    UIUtils.showToast(EnterPwdActivity.this,"密码错误");
                }
                break;

        }
    }
}
