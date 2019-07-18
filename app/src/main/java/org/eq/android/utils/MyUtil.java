package org.eq.android.utils;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import org.eq.android.activity.LoginActivity;
import org.eq.android.api.NetFactory;
import org.eq.android.api.ResponseData;
import org.eq.android.api.VoucherApi;
import org.eq.android.common.Constant;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liufan on 2019/6/13.
 */

public class MyUtil {
    private static long lastClickTime;
    /**
     * 退出登录
     *
     * @param activity
     */
    public static void LoginOut(Activity activity) {
        logout(activity, LoginActivity.class);
    }

    /**
     * 退出登录
     *
     * @param activity
     */
    public static void logout(Activity activity, Class targetClass) {
        Logger.i("退出登录");

        SystemPreferences.remove(Constant.USER_ID);
        SystemPreferences.remove(Constant.ISLOGIN);
        SystemPreferences.remove(Constant.PHONENUMBER);
        SystemPreferences.remove(Constant.USER_ID);
        SystemPreferences.remove(Constant.NICKNAME);
        SystemPreferences.remove(Constant.AUTHSTATUS);
        SystemPreferences.remove(Constant.CLIENTTYPE);
        if (activity != null) {
            Intent intent = new Intent(activity, targetClass);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);

        }
    }

    /**
     * 判断是否登录，如果未登录则跳转到登录界面
     *
     * @return
     */
    public static boolean isLoginDeal(Activity activity) {
        if (!isLogin()) {
            Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show();
            jumpToLogin(activity);
            return false;
        }
        return true;
    }

    /**
     * 判断是否登录
     *
     * @return
     */
    public static boolean isLogin() {
        return SystemPreferences.getLong(Constant.USER_ID)!=0;
    }

    /**
     * 跳转到登录
     *
     * @param activity
     */
    public static void jumpToLogin(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }


    public static void saveLoginInfo(long userId){
        SystemPreferences.save(Constant.USER_ID, userId);
        SystemPreferences.save(Constant.ISLOGIN, true);
    }



    /**
     * 防止多次点击
     * @return
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if ( 0 < timeD && timeD < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 获取动态的密钥
     */
    public void getSupportConfig(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                NetFactory.create(VoucherApi.class).getConfig().enqueue(new Callback<ResponseData>() {
                    @Override
                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {

                    }

                    @Override
                    public void onFailure(Call<ResponseData> call, Throwable t) {

                    }
                });
            }
        }.start();
    }
}
