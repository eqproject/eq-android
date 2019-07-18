package org.eq.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
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
import org.eq.android.entity.User;
import org.eq.android.utils.AESUtils;
import org.eq.android.utils.MyUtil;
import org.eq.android.utils.SystemPreferences;
import org.eq.android.utils.ToastUtil;
import org.eq.android.view.FocusPhoneCode;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liufan on 2019/6/1.
 *
 * 重置密码界面/免密码登录界面
 */

public class ResetPasswordActivity extends BaseActivity{

    @BindView(R.id.constrain_no_pd)
    ConstraintLayout constrain_no_pd;
    @BindView(R.id.no_pd_button)
    RelativeLayout no_pd_button;
    @BindView(R.id.no_pd_text_input_et_EditText)
    EditText no_pd_text_input_et_EditText;
    @BindView(R.id.constrain_getver_code)
    ConstraintLayout constrain_getver_code;
    @BindView(R.id.input_code_textView4)
    TextView input_code_textView4;
    @BindView(R.id.input_code_fpcode)
    FocusPhoneCode input_code_fpcode;
    @BindView(R.id.input_code_button)
    RelativeLayout input_code_button;
    @BindView(R.id.constrain_reast_password)
    ConstraintLayout constrain_reast_password;
    @BindView(R.id.reast_pd_text_input_et_EditText)
    EditText reast_pd_text_input_et_EditText;
    @BindView(R.id.reast_pd_again_input_et_EditText)
    EditText reast_pd_again_input_et_EditText;
    @BindView(R.id.reast_pd_again_button)
    RelativeLayout reast_pd_again_button;
    @BindView(R.id.no_pd_textView2)
    TextView no_pd_textView2;
    @BindView(R.id.reast_password_textView2)
    TextView reast_password_textView2;
    @BindView(R.id.tv_send_ok)
    TextView tv_send_ok;
    @BindView(R.id.tv_reset)
    TextView tv_reset;


    private String phoneNumber;
    private String nameTpye;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_reaset_password;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        nameTpye= getIntent().getStringExtra("nameTpye");
        phoneNumber = getIntent().getStringExtra("phoneNubmer");

        if (!TextUtils.isEmpty(nameTpye) && nameTpye.equals("SETPASSWORD")){
            reast_password_textView2.setText("设置密码");
            constrain_no_pd.setVisibility(View.GONE);
            constrain_getver_code.setVisibility(View.GONE);
            constrain_reast_password.setVisibility(View.VISIBLE);
        }else if(!TextUtils.isEmpty(nameTpye) && nameTpye.equals("SETCODE")){
            constrain_no_pd.setVisibility(View.GONE);
            constrain_getver_code.setVisibility(View.VISIBLE);
            constrain_reast_password.setVisibility(View.GONE);
            input_code_textView4.setText(phoneNumber);
            tv_send_ok.setText("确定");
        }else if(!TextUtils.isEmpty(nameTpye) && nameTpye.equals("ForgetPassword")){
            constrain_no_pd.setVisibility(View.VISIBLE);
            constrain_getver_code.setVisibility(View.GONE);
            constrain_reast_password.setVisibility(View.GONE);
            no_pd_text_input_et_EditText.addTextChangedListener(forgetPasswordListener);
        }else if(!TextUtils.isEmpty(nameTpye) && nameTpye.equals("ChangePassword")){
            reast_password_textView2.setText("修改密码");
            tv_reset.setText("确定");
            constrain_no_pd.setVisibility(View.GONE);
            constrain_getver_code.setVisibility(View.GONE);
            constrain_reast_password.setVisibility(View.VISIBLE);
        }else{
            no_pd_textView2.setText("重置密码");
        }

        reast_pd_text_input_et_EditText.addTextChangedListener(oldPasswrodListener);

    }

    private String mobile;
    private String captche;
    private long userid;
    @OnClick({R.id.input_code_button, R.id.reast_pd_again_button})
    public void onclick(View view){
        switch (view.getId()){
            //获取验证码，后重置密码
            case R.id.input_code_button:

                //重置密码
                if (nameTpye.equals("ForgetPassword")){
                    constrain_reast_password.setVisibility(View.VISIBLE);
                    constrain_getver_code.setVisibility(View.GONE);
                    reast_password_textView2.setText("重置密码");

                    mobile = input_code_textView4.getText().toString().trim();
                    captche = input_code_fpcode.getPhoneCode();
                }

                //注册设置密码
                if (nameTpye.equals("SETCODE")){
                    mobile = input_code_textView4.getText().toString().trim();
                    captche = input_code_fpcode.getPhoneCode();

                    if (!TextUtils.isEmpty(captche)){

                        NetFactory.create(UserApi.class).registerUser(mobile, captche).enqueue(new Callback<ResponseData<OverDue>>() {
                            @Override
                            public void onResponse(Call<ResponseData<OverDue>> call, Response<ResponseData<OverDue>> response) {
                                ResponseData<OverDue> responseData = response.body();
                                if (responseData.getStatus()==200){
                                    constrain_reast_password.setVisibility(View.VISIBLE);
                                    constrain_getver_code.setVisibility(View.GONE);
                                    reast_password_textView2.setText("设置密码");
                                    userid=responseData.getData().getUserId();

                                }else{
                                    ToastUtil.showLongToast(responseData.getErrMsg());
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseData<OverDue>> call, Throwable t) {

                            }
                        });

                    }else{
                        ToastUtil.showLongToast("验证码不能为空");
                    }
                }


                break;
            //重置密码
            case R.id.reast_pd_again_button:

                if (nameTpye.equals("ChangePassword") || nameTpye.equals("SETCODE")){
                    resetPassword();
                }else{
                String password = reast_pd_text_input_et_EditText.getText().toString().trim();
                String againPasswrod = reast_pd_again_input_et_EditText.getText().toString().trim();

                if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(againPasswrod)){
                    if (password.equals(againPasswrod)){
                        password = AESUtils.encrypt(password, Constant.passwordKey); //加密
                        NetFactory.create(UserApi.class).resetPassword(mobile, captche, password).enqueue(new Callback<ResponseData<User>>() {
                            @Override
                            public void onResponse(Call<ResponseData<User>> call, Response<ResponseData<User>> response) {
                                ResponseData<User> result = response.body();
                                if(result.getStatus() == 200){
                                    User user = result.getData();
                                    Toast.makeText(ResetPasswordActivity.this, "设置密码成功", Toast.LENGTH_SHORT).show();
                                    SystemPreferences.save(Constant.ISLOGIN, true);
                                    SystemPreferences.save(Constant.USER_ID,(long)user.getId());
                                    SystemPreferences.save(Constant.PHONENUMBER, user.getMobile());
                                    SystemPreferences.save(Constant.NICKNAME, user.getNickname());
                                    startActivity(new Intent(ResetPasswordActivity.this, MainActivity.class));
                                    finish();
                                }else{
                                    ToastUtil.showLongToast(result.getErrMsg());
                                }

                            }

                            @Override
                            public void onFailure(Call<ResponseData<User>> call, Throwable t) {
                                Toast.makeText(ResetPasswordActivity.this, "设置密码失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        Toast.makeText(this, "密码不能为空，且要两次设置要一样", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "密码不能为空，且要两次设置要一样", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    }
    private TextWatcher oldPasswrodListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int len = s.toString().trim().length();
            if (len<5){
                Toast.makeText(ResetPasswordActivity.this, "密码由字母和阿拉伯数字组合,至少6位",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextWatcher forgetPasswordListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int len = s.toString().trim().length();
            if (len == 11){
                no_pd_button.setBackground(getResources().getDrawable(R.drawable.login_background));
                no_pd_button.setOnClickListener(resetPasswordListener);
            }else{
                no_pd_button.setBackground(getResources().getDrawable(R.drawable.login_white_red));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    int i=0;
    private View.OnClickListener resetPasswordListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            phoneNumber = no_pd_text_input_et_EditText.getText().toString().trim();
            if (!MyUtil.isFastDoubleClick()){
                long time = SystemPreferences.getLong(phoneNumber);
                long nowTime = System.currentTimeMillis();
                if ((nowTime-time)>1000*60*5){
                    setSmsCode();
                    Log.e(".............", "onClick: "+i );
                }else {
                    constrain_no_pd.setVisibility(View.GONE);
                    constrain_getver_code.setVisibility(View.VISIBLE);
                    input_code_textView4.setText(phoneNumber);
                    Toast.makeText(ResetPasswordActivity.this, "短信时效5分钟", Toast.LENGTH_LONG).show();
                }
            }

        }
    };

    /**
     * 发送验证码
     */
    private void setSmsCode(){
        phoneNumber = no_pd_text_input_et_EditText.getText().toString().trim();
        if (!TextUtils.isEmpty(phoneNumber)){
            SystemPreferences.save(phoneNumber, System.currentTimeMillis());
            NetFactory.create(UserApi.class).sendSms(phoneNumber,1).enqueue(new Callback<ResponseData>() {
                @Override
                public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                    ResponseData result = response.body();
                    if (result.getStatus() == 200){
                        SystemPreferences.save(phoneNumber, System.currentTimeMillis());
                        constrain_no_pd.setVisibility(View.GONE);
                        constrain_getver_code.setVisibility(View.VISIBLE);
                        input_code_textView4.setText(phoneNumber);
                    }else{
                        Toast.makeText(ResetPasswordActivity.this, result.getErrMsg(), Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onFailure(Call<ResponseData> call, Throwable t) {
                    Toast.makeText(ResetPasswordActivity.this, "手机号不对或获取验证码失败", Toast.LENGTH_SHORT).show();
                }
            });

        }else {
            Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            SystemPreferences.remove(Constant.PHONENUMBER);
        }
        return super.onKeyDown(keyCode, event);
    }

    private void resetPassword(){
        String password = reast_pd_text_input_et_EditText.getText().toString().trim();
        String againPasswrod = reast_pd_again_input_et_EditText.getText().toString().trim();
        if (nameTpye.equals("ChangePassword")){
            userid =SystemPreferences.getLong(Constant.USER_ID);
        }
        if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(againPasswrod)){
            if (password.equals(againPasswrod)){
                password = AESUtils.encrypt(password, Constant.passwordKey); //加密
                NetFactory.create(UserApi.class).resetPassword(userid, password).enqueue(new Callback<ResponseData<User>>() {
                    @Override
                    public void onResponse(Call<ResponseData<User>> call, Response<ResponseData<User>> response) {
                        ResponseData<User> result = response.body();
                        if(result.getStatus() == 200){
                            User user = result.getData();
                            Toast.makeText(ResetPasswordActivity.this, "设置密码成功", Toast.LENGTH_SHORT).show();
                            SystemPreferences.save(Constant.ISLOGIN, true);
                            SystemPreferences.save(Constant.USER_ID,(long)user.getId());
                            SystemPreferences.save(Constant.PHONENUMBER, user.getMobile());
                            SystemPreferences.save(Constant.NICKNAME, user.getNickname());
                            startActivity(new Intent(ResetPasswordActivity.this, MainActivity.class));
                            finish();
                        }else{
                            ToastUtil.showLongToast(result.getErrMsg());
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseData<User>> call, Throwable t) {
                        Toast.makeText(ResetPasswordActivity.this, "设置密码失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                Toast.makeText(this, "密码不能为空，且要两次设置要一样", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "密码不能为空，且要两次设置要一样", Toast.LENGTH_SHORT).show();
        }
    }

}
