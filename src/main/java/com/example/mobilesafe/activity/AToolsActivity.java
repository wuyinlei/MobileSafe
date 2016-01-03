package com.example.mobilesafe.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafe.R;
import com.example.mobilesafe.utils.SMSUtils;
import com.example.mobilesafe.utils.UIUtils;

/**
 * 高级工具
 */
public class AToolsActivity extends AppCompatActivity {


    private ProgressDialog pd;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
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

        //加入了一个进度条
        pd = new ProgressDialog(AToolsActivity.this);
        pd.setTitle("提示");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在备份，请稍等");
        pd.show();

        //这个在重新启动一个线程，来弹出安全吐司，当然这个地方用到了
        new Thread(){
            @Override
            public void run() {
                super.run();
                boolean result = SMSUtils.backUpSms(AToolsActivity.this, new SMSUtils.BackUpCallbackSms() {
                    @Override
                    public void before(int count) {
                        pd.setMax(count);
                        progressBar.setMax(count);
                    }

                    @Override
                    public void onBackUpSms(int process) {
                        pd.setProgress(process);
                        progressBar.setProgress(process);
                    }
                });
                if (result) {
                    UIUtils.showToast(AToolsActivity.this, "备份成功");
                } else {
                    UIUtils.showToast(AToolsActivity.this, "备份失败");
                }
                pd.dismiss();
            }
        }.start();


    }
}
