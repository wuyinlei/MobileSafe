package com.example.mobilesafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafe.R;

import java.lang.reflect.Field;

public class DragViewActivity extends AppCompatActivity {

    //sbar为状态栏的高度
    int x = 0, sbar = 0;
    private int startX, startY;
    private TextView tvTop;
    private TextView tvBottom;
    private ImageView ivdrag;
    private SharedPreferences mPres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_view);
        mPres = getSharedPreferences("config", MODE_PRIVATE);
        initialize();
        reDraw();
    }

    /**
     * 初始化布局控件
     */
    private void initialize() {
        tvTop = (TextView) findViewById(R.id.tvTop);
        tvBottom = (TextView) findViewById(R.id.tvBottom);
        ivdrag = (ImageView) findViewById(R.id.iv_drag);
    }

    /**
     * 重新绘制布局
     */
    private void reDraw() {
        int lastX = mPres.getInt("lastX", 0);
        int lastY = mPres.getInt("lastY", 0);


        //初始化布局的步奏
        //onMeasure(测量view)   onLayout(放置的位置)   onDraw(绘制)
        // ivdrag.layout(lastX,lastY,lastX+ivdrag.getWidth(),lastY+ivdrag.getHeight());
        // 不能用此方法，因为我们还没有测量完成就不能安放位置

        //获取屏幕的宽高，这个包含了状态栏的高度
        final int winWidth = getWindowManager().getDefaultDisplay().getWidth();
        final int winHeight = getWindowManager().getDefaultDisplay().getHeight();

        //根据图片的位置来设置提示框的显示位置
        if (lastY > winHeight/2){   //上边的显示，下部的隐藏
            tvTop.setVisibility(View.VISIBLE);
            tvBottom.setVisibility(View.INVISIBLE);
        } else {
            tvTop.setVisibility(View.INVISIBLE);
            tvBottom.setVisibility(View.VISIBLE);
        }

        //拿到布局的参数   加载子控件的。
        //父标签是谁，下面的LayoutParams之前的就是谁
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ivdrag.getLayoutParams();
        layoutParams.leftMargin = lastX;
        layoutParams.topMargin = lastY;

        //重新设置位置
        ivdrag.setLayoutParams(layoutParams);

        //设置拖拽监听
        ivdrag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:   //手势按下的状态
                        //获取初始坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:   //手势移动的状态
                        //获取移动后的坐标
                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();

                        //计算偏移量
                        int dx = endX - startX;
                        int dy = endY - startY;

                        //根据偏移量来更新左上右下坐标的距离
                        int l = ivdrag.getLeft() + dx;
                        int r = ivdrag.getRight() + dx;
                        int t = ivdrag.getTop() + dy;
                        int b = ivdrag.getBottom() + dy;


                        /**
                         * 在这里通过反射的机制来获取到状态栏的高度，
                         * 同理还可以获取到actionBar的高度
                         */
                        Class<?> aClass = null;
                        Object obj = null;
                        Field field = null;
                        try {
                            aClass = Class.forName("com.android.internal.R$dimen");
                            obj = aClass.newInstance();
                            field = aClass.getField("status_bar_height");
                            x = Integer.parseInt(field.get(obj).toString());
                            sbar = getResources().getDimensionPixelSize(x);
                        } catch(Exception e1) {
                            e1.printStackTrace();
                        }

                        //如果满足了一下的情况，就直接break，一下的情况是
                        //出左侧边界，出上部边界，出下部边界，出右侧边界
                        if (l<0 || r>winWidth || t<0 || b>winHeight-sbar){
                            break;
                        }

                        //根据图片的位置来设置提示框的显示位置
                        if (t > winHeight/2){   //上边的显示，下部的隐藏
                            tvTop.setVisibility(View.VISIBLE);
                            tvBottom.setVisibility(View.INVISIBLE);
                        } else {
                            tvTop.setVisibility(View.INVISIBLE);
                            tvBottom.setVisibility(View.VISIBLE);
                        }
                        //更新界面
                        /**
                         * @param l Left position, relative to parent
                         * @param t Top position, relative to parent
                         * @param r Right position, relative to parent
                         * @param b Bottom position, relative to parent
                         */
                        ivdrag.layout(l, t, r, b);
                        //重新初始化起点坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:   //手势抬起的状态

                        //记录抬起后的坐标点，然后我们保存并且记录，来让下一次重新进入的时候更新位置
                        SharedPreferences.Editor edit = mPres.edit();
                        edit.putInt("lastX", ivdrag.getLeft());
                        edit.putInt("lastY", ivdrag.getTop());
                        edit.commit();
                        break;
                }
                return true;
            }
        });
    }
}
