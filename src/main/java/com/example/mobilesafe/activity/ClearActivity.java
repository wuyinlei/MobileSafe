package com.example.mobilesafe.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.example.mobilesafe.R;

/**
 * 缓存清理
 */
public class ClearActivity extends AppCompatActivity {

    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear);
        initialize();
    }

    /**
     * 初始化布局控件
     */
    private void initialize() {

        listview = (ListView) findViewById(R.id.list_view);
    }
}
