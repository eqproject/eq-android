package org.eq.android.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tencent.bugly.crashreport.CrashReport;

import org.eq.android.utils.SystemPreferences;

import java.util.Date;

public class EQApplication extends Application {

    private static EQApplication instance;
    //判断是否在前后台
    private int count = 0;
    //第一次不对程序进行处理里
    private boolean isFirst = true;
    private boolean isBack = false;
    private long time = -2;


    /**
     * 本地键值对
     */
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();

        CrashReport.initCrashReport(getApplicationContext(), "1fc00a9f4d", true);

        instance = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // 日志配置
        Logger.addLogAdapter(new AndroidLogAdapter() {
            @Override
            public boolean isLoggable(int priority, @Nullable String tag) {
                return Constant.debug;
            }
        });


        //app退到后台十分钟重新登录
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(final Activity activity) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    if (count == 0) {
                        Date date = new Date();
                        //回到前台的时间
                        long returntime = date.getTime();
                        //程序在后台运行的时间
                        long sub = returntime - time;
                        Log.v("app", ">>>>>>>>>>>>>>>>>>>切到前台  lifecycle");
                        //时间小于600秒或者启动的activity为首页则不执行操作
                        if (sub < 600000 || activity.getClass().getName().equals("org.eq.android.activity.LoginActivity")) {}
                        else {
                            SystemPreferences.remove(Constant.ISLOGIN);
                            SystemPreferences.remove(Constant.PHONENUMBER);
                            SystemPreferences.remove(Constant.USER_ID);
                            SystemPreferences.remove(Constant.NICKNAME);
                            SystemPreferences.remove(Constant.AUTHSTATUS);
                            SystemPreferences.remove(Constant.CLIENTTYPE);
                            new AlertDialog.Builder(activity)
                                    .setTitle("提示消息")
                                    .setMessage("请重新登录")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                           /* String callbackActivity = activity.getClass().getName();
                                            //跳转到登录界面
                                            Intent i = new Intent(activity, LoginActivity.class);
                                            i.putExtra("target", callbackActivity);
                                            //将传入当前activity的数据保存,以便在跳转回来时不报错
                                            Bundle params = activity.getIntent().getExtras();
                                            if (params != null) {
                                                i.putExtras(params);
                                            }
                                            activity.startActivity(i);*/
//                                           MyUtil.LoginOut(activity);
                                            dialog.dismiss();
                                        }
                                    })
                                    .setOnKeyListener(new DialogInterface.OnKeyListener() {
                                        @Override
                                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                            //禁用返回键,要求必须重新登录
                                            if(keyCode==KeyEvent.KEYCODE_BACK){
                                                return true;
                                            }else {
                                                return false;
                                            }

                                        }
                                    }).show();
                        }
                    }
                }
                count++;
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                count--;
                if (count == 0) {
                    Log.v("app", ">>>>>>>>>>>>>>>>>>>切到后台  lifecycle");
                    Date date = new Date();
                    //保存上一次在后台的时间
                    time = date.getTime();
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });

    }


    public static EQApplication getInstance() {
        return instance;
    }

    public SharedPreferences getPreferences() {
        return sharedPreferences;
    }

}
