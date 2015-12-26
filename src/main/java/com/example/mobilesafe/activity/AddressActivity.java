package com.example.mobilesafe.activity;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mobilesafe.R;
import com.example.mobilesafe.db.AddressDao;

/**
 * 归属地查询页面
 */
public class AddressActivity extends AppCompatActivity {

    private EditText editNumber;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        initialize();
    }

    private void initialize() {

        editNumber = (EditText) findViewById(R.id.editNumber);

        //动态的监听EditText的变化
        editNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String address = AddressDao.getAddress(s.toString().trim());
                tvResult.setText(address);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tvResult = (TextView) findViewById(R.id.tvResult);
    }

    /**
     * 查询
     *
     * @param view
     */
    public void query(View view) {
        String number = editNumber.getText().toString().trim();
        if (!TextUtils.isEmpty(number)) {
            String address = AddressDao.getAddress(number);
            tvResult.setText(address);
        } else {
            //震动的动画
            Animation shake = AnimationUtils.loadAnimation(this,R.anim.shake);
            editNumber.startAnimation(shake);
            vibrate();
        }
    }

    /**
     * 手机震动
     */
    private void vibrate(){
        //或去震动服务
        Vibrator vibrate = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        //vibrate.vibrate(200);   //震动2秒
        //先等待1秒，在震动2秒，等待1秒，震动3秒，
        // -1表示执行一次  0则是从头循环  参2 表示是从第几个循环开始
        vibrate.vibrate(new long[]{1000,2000,1000,3000},-1);

        vibrate.cancel();
    }
}
