package com.example.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.mobilesafe.R;
import com.example.mobilesafe.view.SettingItemView;

/**
 * 第二个引导页
 */
public class Setup2Activity extends BaseSetupActivity {


    private SettingItemView simBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_2);

        initialize();
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
        //如果sim卡没有绑定，就不允许进入下一个界面
        String sim = mPres.getString("sim", null);
        if (TextUtils.isEmpty(sim)) {
            Toast.makeText(this, "必须绑定SIM卡", Toast.LENGTH_SHORT).show();
            return;
        }

        startActivity(new Intent(Setup2Activity.this, Setup3Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_out, R.anim.tran_in);
    }

    private void initialize() {
        simBind = (SettingItemView) findViewById(R.id.simBind);
        bindSimMessage();
    }

    /**
     * 是否绑定sim卡的信息
     */
    private void bindSimMessage() {
        String sim = mPres.getString("sim", null);
        if (!TextUtils.isEmpty(sim)){
            simBind.setChecked(true);
        } else {
            simBind.setChecked(false);
        }
        simBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (simBind.isChecked()) {
                    simBind.setChecked(false);
                    //删除已经绑定的sim卡
                    mPres.edit().remove("sim").commit();
                } else {
                    simBind.setChecked(true);
                    //保存sim卡的信息
                    TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    String simSeraNumber = tm.getSimSerialNumber(); //获取sim卡的序列号
                    mPres.edit().putString("sim", simSeraNumber).commit(); //保存sim卡的序列号

                }
            }
        });
    }
}
