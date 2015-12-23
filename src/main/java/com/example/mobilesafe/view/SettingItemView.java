package com.example.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mobilesafe.R;

/**
 * Created by 若兰 on 2015/12/23.
 */
public class SettingItemView extends RelativeLayout {

    private TextView tvTitle,tvDesc;
    private CheckBox cbStatus;

    public SettingItemView(Context context) {
        this(context, null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化布局
     */
    private void initView(){
        //将自定义好的布局文件给当前的SettingItemView
        View.inflate(getContext(), R.layout.view_setting_item,this);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvDesc = (TextView) findViewById(R.id.tv_desc);
        cbStatus = (CheckBox) findViewById(R.id.cb_status);
    }

    public void setTitle(String title){
        tvTitle.setText(title);
    }

    public void setDesc(String desc){
        tvDesc.setText(desc);
    }

    //返回勾选状态
    public boolean isChecked(){
        return cbStatus.isChecked();
    }

    public void setChecked(boolean check){
        cbStatus.setChecked(check);
    }
}
