package org.eq.android.utils;

import android.content.Context;

/**
 * 状态栏 工具类
 */
public class StatusBarHeightUtil {

    /**
     * 拿到状态栏高度
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int height = 0;
        int resourceId = context.getApplicationContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = context.getApplicationContext().getResources().getDimensionPixelSize(resourceId);
        }
        return height;
    }

    /**
     * 原图来自 ios 尺寸，android 状态栏比较高
     * @param context
     * @param height
     * @return
     */
    public static int subIOStatusBarHeight(Context context, int height) {
        return height + getStatusBarHeight(context) - 40;
    }
}
