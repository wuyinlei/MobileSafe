package com.example.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.mobilesafe.R;
import com.example.mobilesafe.adapter.HomeAdapter;

/**
 * Created by 若兰 on 2015/12/23.
 */
public class HomeActivity extends AppCompatActivity {

    private HomeAdapter homeAdapter;
    private GridView gv_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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
                switch (position){
                    case 8:
                        startActivity(new Intent(HomeActivity.this,SettingActivity.class));
                        break;
                    default:
                        break;
                }
            }
        });
    }

}
