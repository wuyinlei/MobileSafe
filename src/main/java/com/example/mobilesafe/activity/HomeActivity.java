package com.example.mobilesafe.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafe.R;
import com.example.mobilesafe.adapter.HomeAdapter;
import com.example.mobilesafe.utils.MD5Utils;

/**
 * Created by 若兰 on 2015/12/23.
 */
public class HomeActivity extends AppCompatActivity {

    private SharedPreferences mPref;
    private HomeAdapter homeAdapter;
    private GridView gv_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mPref = getSharedPreferences("config", MODE_PRIVATE);
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        gv_home = (GridView) findViewById(R.id.gv_home);
        homeAdapter = new HomeAdapter(getApplicationContext());
        gv_home.setAdapter(homeAdapter);

        //设置监听
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //手机防盗
                        showPassword();
                        break;
                    case 1:
                        startActivity(new Intent(HomeActivity.this,CallSafeActivity.class));
                        break;
                    case 7:
                        startActivity(new Intent(HomeActivity.this,AToolsActivity.class));
                        break;
                    case 8:
                        startActivity(new Intent(HomeActivity.this, SettingActivity.class));
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 显示密码弹窗
     */
    private void showPassword() {
        //判断是否设置密码
        //如果没有设置过，弹出没有设置密码的弹窗
        String password = mPref.getString("password", null);
        if (!TextUtils.isEmpty(password)) {   //如果之前设置了密码，就直接输入密码
            showPasswordInputDialog();
        } else {//否则就要显示设置密码的弹窗
            showPasswordDialog();
        }
    }

    /**
     * 输入密码弹窗
     */
    private void showPasswordInputDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        //将自定义的布局文件个dialog
        View view = View.inflate(this, R.layout.input_password, null);
        //dialog.setView(view);
        dialog.setView(view, 0, 0, 0, 0);  //设置边距为0
        final EditText etPassword = (EditText) view.findViewById(R.id.et_password);
        Button btnOk = (Button) view.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();
                String savePassword = mPref.getString("password", null);
                if (!TextUtils.isEmpty(password)) {
                    //要进行MD5加密之后在对比
                    if (MD5Utils.encode(password).equals(savePassword)) {
                        // Toast.makeText(HomeActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        //跳转到手机防盗页面
                        dialog.dismiss();
                        startActivity(new Intent(HomeActivity.this, LostFoubdActivity.class));

                    } else {
                        Toast.makeText(HomeActivity.this, "输入的密码不正确", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button btnCan = (Button) view.findViewById(R.id.btnCan);
        btnCan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();   //隐藏dialog
            }
        });
        dialog.show();
    }

    /**
     * 设置密码弹窗
     */
    private void showPasswordDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        //将自定义的布局文件个dialog
        View view = View.inflate(this, R.layout.dialog_set_password, null);
        //dialog.setView(view);
        dialog.setView(view, 0, 0, 0, 0);  //设置边距为0
        final EditText etPassword = (EditText) view.findViewById(R.id.et_password);
        final EditText etConfirm = (EditText) view.findViewById(R.id.et_cofirm);
        Button btnOk = (Button) view.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();
                String confirm = etConfirm.getText().toString();
                if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirm)) {
                    if (password.equals(confirm)) {
                        /// Toast.makeText(HomeActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        //对密码进行MD5加密处理
                        mPref.edit().putString("password", MD5Utils.encode(password)).commit();
                        //跳转到防盗页面
                        dialog.dismiss();
                        startActivity(new Intent(HomeActivity.this, LostFoubdActivity.class));
                    } else {
                        Toast.makeText(HomeActivity.this, "您输入的两次密码不一致", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "输入框不能为空!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button btnCan = (Button) view.findViewById(R.id.btnCan);
        btnCan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();   //隐藏dialog
            }
        });
        dialog.show();
    }

}
