package org.eq.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.eq.android.R;
import org.eq.android.api.NetFactory;
import org.eq.android.api.ResponseData;
import org.eq.android.api.UserApi;
import org.eq.android.common.Constant;
import org.eq.android.common.EQApplication;
import org.eq.android.entity.OverDue;
import org.eq.android.utils.MyUtil;
import org.eq.android.utils.SystemPreferences;

import java.util.logging.Logger;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liufan on 2019/6/1.
 */

public class RegisterActivity extends BaseActivity{

    @BindView(R.id.et_phone)
    EditText et_phone;
    @BindView(R.id.et_code)
    EditText et_code;
    @BindView(R.id.send_code)
    RelativeLayout send_code;
    @BindView(R.id.register_button)
    RelativeLayout register_button;
    @BindView(R.id.login)
    TextView login;

    private String phoneNumber1;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_register;
    }

    @Override
    public void init(Bundle savedInstanceState) {

        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int len = s.toString().trim().length();
                Log.e("...", "onTextChanged: "+count );
                if (len == 11){
                    register_button.setBackground(getResources().getDrawable(R.drawable.login_background));
                    register_button.setOnClickListener(registerListenr);
                }else{
                    register_button.setBackground(getResources().getDrawable(R.drawable.login_white_red));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick({R.id.send_code,R.id.login})
    public void onclick(View view) {
        switch (view.getId()) {
            //发送验证码
            case R.id.send_code:


                break;
            //密码登录
            case R.id.login:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
    }

    private View.OnClickListener registerListenr = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (!MyUtil.isFastDoubleClick()){
                phoneNumber1 = et_phone.getText().toString().trim();
                long time = SystemPreferences.getLong(phoneNumber1);
                long nowTime = System.currentTimeMillis();
                if ((nowTime-time)>1000*60*5){
                    sendCode();
                }else {
                    Intent intent =new Intent(RegisterActivity.this, ResetPasswordActivity.class);
                    intent.putExtra("nameTpye", "SETCODE");
                    intent.putExtra("phoneNubmer", phoneNumber1);
                    startActivity(intent);
                    Toast.makeText(RegisterActivity.this, "发送短信时效5分钟", Toast.LENGTH_SHORT).show();
                }
            }

//            registerButton();
        }
    };

    private void sendCode(){
      phoneNumber1 = et_phone.getText().toString().trim();
        if (!TextUtils.isEmpty(phoneNumber1)){
            NetFactory.create(UserApi.class).sendSms(phoneNumber1,1).enqueue(new Callback<ResponseData>() {
                @Override
                public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                    if (response.body().getStatus() == 200){
                        SystemPreferences.save(phoneNumber1,System.currentTimeMillis());
                        Toast.makeText(RegisterActivity.this, "获取验证码成功", Toast.LENGTH_SHORT).show();
                        Intent intent =new Intent(RegisterActivity.this, ResetPasswordActivity.class);
                        intent.putExtra("nameTpye", "SETCODE");
                        intent.putExtra("phoneNubmer", phoneNumber1);
                        startActivity(intent);
                    }else{
                        Toast.makeText(RegisterActivity.this, response.body().getErrMsg(), Toast.LENGTH_LONG).show();
                    }


                }

                @Override
                public void onFailure(Call<ResponseData> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, "获取验证码失败", Toast.LENGTH_SHORT).show();
                }
            });

        }else{
            Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();
        }
    }


    private void registerButton(){
        String phoneNumber = et_phone.getText().toString().trim();
        String code = et_code.getText().toString().trim();

        if (!TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(code)){
            NetFactory.create(UserApi.class).registerUser(phoneNumber,code).enqueue(new Callback<ResponseData<OverDue>>() {
                @Override
                public void onResponse(Call<ResponseData<OverDue>> call, Response<ResponseData<OverDue>> response) {
                    ResponseData<OverDue> result = response.body();
                    Log.e(".....", "onResponse: "+result.toString() );
                    //200：成功
                    //301:验证码错误
                    //status 1
                    com.orhanobut.logger.Logger.e("register",response);
//                    int userid = result.getData().getUserId();
                    Intent intent = new Intent(RegisterActivity.this, ResetPasswordActivity.class);
                    intent.putExtra("nameTpye", "SETPASSWORD");
                    startActivity(intent);
                }

                @Override
                public void onFailure(Call<ResponseData<OverDue>> call, Throwable t) {
                    com.orhanobut.logger.Logger.e("register",t+"........"+call);
                }
            });

        }else{
            Toast.makeText(this, "手机号跟验证码都不能为空", Toast.LENGTH_SHORT).show();
        }
    }
}
