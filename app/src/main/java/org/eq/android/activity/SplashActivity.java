package org.eq.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.githang.statusbar.StatusBarCompat;
import org.eq.android.R;
import org.eq.android.common.Constant;
import org.eq.android.utils.SystemPreferences;


/**
 * Created by liufan on 2019/5/30.
 */

public class SplashActivity extends AppCompatActivity {
    private int time=1;
    private boolean isLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.colorwhite) , true);
        isLogin = SystemPreferences.getBoolean(Constant.ISLOGIN);
        handler.postDelayed(runnable,500);
    }

    Handler handler=new Handler();
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            time--;
            handler.postDelayed(this,500);

            if(time==0){
                if (isLogin){
                    Intent intent=new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(intent);
                }else{
                    //现在不登陆页跳转到 main
                    Intent  intent= new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                }

                finish();
            }
        }
    };

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK ) {
            //这个界面啥都不让做，死等自动关闭
            return true;
        }else {
            return super.dispatchKeyEvent(event);
        }
    }
}
