package org.eq.android.common;

import android.content.Context;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.zhengsr.viewpagerlib.indicator.TabIndicator;

import java.lang.reflect.Field;

/**
 * 自定义实现圆角 indicator
 */
public class MyTabIndicator extends TabIndicator {
    public MyTabIndicator(Context context) {
        super(context);
    }

    public MyTabIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTabIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //反射拿到不能被修改的字段
        Class<?> clazz = this.getClass().getSuperclass();
        try {
            Field mWidth = clazz.getDeclaredField("mWidth");
            Field mCount = clazz.getDeclaredField("mCount");
            Field mPath = clazz.getDeclaredField("mPath");
            Field mTabWidth = clazz.getDeclaredField("mTabWidth");
            Field mTabHeight = clazz.getDeclaredField("mTabHeight");
            //让属性可以被访问
            mWidth.setAccessible(true);
            mCount.setAccessible(true);
            mPath.setAccessible(true);
            mTabWidth.setAccessible(true);
            mTabHeight.setAccessible(true);

            Path path = new Path();
            int width = (int) mWidth.get(this) / (int) mCount.get(this);
            int tabHeight = (int) mTabHeight.get(this);
            int tabWidth = (int) mTabWidth.get(this);


            RectF rectF = new RectF((width - tabWidth) / 2, h - tabHeight, (width + tabWidth) / 2, h);

            path.addRoundRect(rectF, tabHeight / 2, tabHeight / 2, Path.Direction.CW);

            path.close();

            //给属性设置新的值
            mPath.set(this, path);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
