package com.example.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by 若兰 on 2015/12/23.
 *
 * 获取焦点的TextView
 */
public class FocusedView extends TextView {

    //用代码new对象的时候走此方法
    public FocusedView(Context context) {
        this(context, null);
    }

    //有属性的时候走此方法
    public FocusedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    //有style样式的时候走此方法
    public FocusedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 表示有没有获取焦点
     * @return
     *
     * 跑马灯要运行，首先要调用此方法，判断时候有焦点，如果是true，就会有效果
     *
     * 我们强制的返回true，我们不管实际上TextView是否有焦点，让跑马灯认为有焦点
     */
    @Override
    public boolean isFocused() {
        return true;
    }
}
