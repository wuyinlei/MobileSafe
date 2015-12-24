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
public class SettingItemView extends RelativeLayout {

    /**
     * 1、自定义一个View，继承ViewGroup，比如RelativeLayout
     * 2、编写组合控件的布局文件，在布局文件中加载  View.inflate(getContext(), R.layout.view_setting_item, this);
     * 3、自定义属性，在attrs中定义
     * 4、title = attrs.getAttributeValue(NAMESPACE, "title_update")方式来获取已经定义好的属性
     */

    private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.example.mobilesafe";
    private TextView tvTitle, tvDesc;
    private CheckBox cbStatus;
    private String title;
    private String desc_on;
    private String desc_off;

    public SettingItemView(Context context) {
        this(context, null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        title = attrs.getAttributeValue(NAMESPACE, "title_update");   //根据属性名称获取属性的值
        desc_on = attrs.getAttributeValue(NAMESPACE, "desc_on");
        desc_off = attrs.getAttributeValue(NAMESPACE, "desc_off");
        initView();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        //将自定义好的布局文件给当前的SettingItemView
        View.inflate(getContext(), R.layout.view_setting_item, this);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText(title);
        tvDesc = (TextView) findViewById(R.id.tv_desc);
        cbStatus = (CheckBox) findViewById(R.id.cb_status);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setDesc(String desc) {
        tvDesc.setText(desc);
    }

    //返回勾选状态
    public boolean isChecked() {
        return cbStatus.isChecked();
    }

    public void setChecked(boolean check) {
        cbStatus.setChecked(check);

        //根据选择的状态更新文本
        if (check) {
            setDesc(desc_on);
        } else {
            setDesc(desc_off);
        }
    }
}
