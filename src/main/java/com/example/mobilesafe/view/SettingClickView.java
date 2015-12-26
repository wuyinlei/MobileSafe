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
 * <p/>
 * 自定义的组合控件
 */
public class SettingClickView extends RelativeLayout {

    /**
     * 1、自定义一个View，继承ViewGroup，比如RelativeLayout
     * 2、编写组合控件的布局文件，在布局文件中加载  View.inflate(getContext(), R.layout.view_setting_item, this);
     * 3、自定义属性，在attrs中定义
     * 4、title = attrs.getAttributeValue(NAMESPACE, "title_update")方式来获取已经定义好的属性
     */

    private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.example.mobilesafe";
    private TextView tvTitle, tvDesc;

    public SettingClickView(Context context) {
        this(context, null);
    }

    public SettingClickView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        //将自定义好的布局文件给当前的SettingClickView
        View.inflate(getContext(), R.layout.view_setting_click, this);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvDesc = (TextView) findViewById(R.id.tv_desc);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setDesc(String desc) {
        tvDesc.setText(desc);
    }


}
