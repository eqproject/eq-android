package org.eq.android.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import org.eq.android.R;
import org.eq.android.api.NetFactory;
import org.eq.android.api.ResponseData;
import org.eq.android.api.UserApi;
import org.eq.android.common.Constant;
import org.eq.android.common.EQApplication;
import org.eq.android.entity.User;
import org.eq.android.utils.AESUtils;
import org.eq.android.utils.MyUtil;
import org.eq.android.utils.SystemPreferences;
import org.eq.android.view.SlidingView;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by liufan on 2019/6/1.
 */

public class LoginActivity extends BaseActivity{

    private static final String TAG = "LoginActivity";

    @BindView(R.id.text_input_user_layout)
    TextInputLayout textInputUserNameLayout;
    @BindView(R.id.text_input_et_EditText)
    EditText text_input_et_EditText;
    @BindView(R.id.text_input_password)
    TextInputLayout textInputPasswordLayout;
    @BindView(R.id.text_input_et_password)
    EditText text_input_et_password;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.forgetpd)
    TextView forgetpd;
    @BindView(R.id.phont_passwor_view_err)
    Button phont_passwor_view_err;
    @BindView(R.id.constrain_login)
    ConstraintLayout constrain_login;
    @BindView(R.id.constrain_no_pd)
    ConstraintLayout constrain_no_pd;
    @BindView(R.id.no_pd_textView)
    TextView no_pd_textView;
    @BindView(R.id.no_pd_text_input_et_EditText)
    EditText no_pd_text_input_et_EditText;
    @BindView(R.id.no_pd_button)
    RelativeLayout no_pd_button;
    @BindView(R.id.button)
    RelativeLayout button;
    @BindView(R.id.register)
    TextView register;
    @BindView(R.id.tv_error)
    RelativeLayout tv_error;

    private View mPopupHeadViewy;
    private PopupWindow mHeadPopupclly;
    private SlidingView pop_slidingview;

    private String  phoneNumber;
    private boolean password_error;


    @Override
    public int getContentViewResId() {
        return R.layout.activity_login;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.colorwhite) , true);

        text_input_et_EditText.addTextChangedListener(textchangedListener);
    }

    private TextWatcher textchangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            if(charSequence.length()<11){
                Log.e(TAG, "onTextChanged: ");
                textInputUserNameLayout.setError("手机格式不正确");
                textInputUserNameLayout.setErrorEnabled(true);
            }else {
                textInputUserNameLayout.setErrorEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    @OnClick({R.id.textView,R.id.forgetpd, R.id.no_pd_textView,R.id.no_pd_button, R.id.button,R.id.register})
    public void onclick(View view){
        switch (view.getId()){
            //切换到免密码登录
            case R.id.textView:
                constrain_login.setVisibility(View.GONE);
                constrain_no_pd.setVisibility(View.VISIBLE);
                break;
            //忘记密码/重置密码
            case R.id.forgetpd:
                Intent intent = new Intent(this, ResetPasswordActivity.class);
                intent.putExtra("nameTpye", "ForgetPassword");
                startActivity(intent);
                break;
            //切换到登录界面
            case R.id.no_pd_textView:
                constrain_login.setVisibility(View.VISIBLE);
                constrain_no_pd.setVisibility(View.GONE);
                break;
            //获取验证码
            case R.id.no_pd_button:
                phoneNumber = no_pd_text_input_et_EditText.getText().toString().trim();
                if (!TextUtils.isEmpty(phoneNumber)){
                    long time = SystemPreferences.getLong(phoneNumber);
                    long nowTime = System.currentTimeMillis();

                    if ((nowTime-time)>1000*60*5){
                        showPopupWindowSlidingView();
                    }else {
                        Toast.makeText(LoginActivity.this, "发送短袖时效5分钟", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
            //登录
            case R.id.button:
                if(!MyUtil.isFastDoubleClick()){
                    final String phoneumber = text_input_et_EditText.getText().toString().trim();
                    String password = text_input_et_password.getText().toString().trim();
                    if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(phoneumber)){
                        password = AESUtils.encrypt(password, Constant.passwordKey); //加密
                        //登录
                        NetFactory.create(UserApi.class).login(phoneumber, password).enqueue(new Callback<ResponseData<User>>() {
                            @Override
                            public void onResponse(Call<ResponseData<User>> call, Response<ResponseData<User>> response) {
                                ResponseData<User> responseData = response.body();
                                if (responseData.getStatus() == 200 && responseData.getData() != null){
                                 MyUtil.saveLoginInfo(responseData.getData().getId());
                                 SystemPreferences.save(Constant.PHONENUMBER, phoneumber);
                                 SystemPreferences.save(Constant.AUTHSTATUS, responseData.getData().getAuthStatus());
                                 SystemPreferences.save(Constant.NICKNAME, responseData.getData().getNickname());
                                 SystemPreferences.save(Constant.CLIENTTYPE, responseData.getData().getClientType());
                                 startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                 finish();
                                }else if(responseData.getStatus() == 302){
                                    Toast.makeText(LoginActivity.this, responseData.getErrMsg(),Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onFailure(Call<ResponseData<User>> call, Throwable t) {

                            }
                        });

                    }else{
                        Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            //注册
            case R.id.register:
                startActivity(new Intent(this, RegisterActivity.class));
                finish();
                break;
        }

    }


    private View contentViewi;
    //滑动滑块
    private void showPopupWindowSlidingView(){

//        EQApplication.getInstance().setTimeLimit(System.currentTimeMillis());


        //自定义的弹窗布局
        contentViewi=View.inflate(this,R.layout.popup_slidingview,null);
        pop_slidingview = (SlidingView) contentViewi.findViewById(R.id.pop_slidingview);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(contentViewi);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();


        pop_slidingview.setOnVerifyListener(new SlidingView.OnVerifyListener() {
            @Override
            public void success() {
                String password = no_pd_text_input_et_EditText.getText().toString().trim();
                NetFactory.create(UserApi.class).sendSms(password,1).enqueue(new Callback<ResponseData>() {
                    @Override
                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                        ResponseData responseData =response.body();
                        if (responseData.getStatus()==200){
                            SystemPreferences.save(phoneNumber, System.currentTimeMillis());
                            startActivity(new Intent(LoginActivity.this, SetPasswordActivity.class));
                            alertDialog.dismiss();
                        }else{
                            Toast.makeText(LoginActivity.this, response.body().getErrMsg(), Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseData> call, Throwable t) {
                        Log.e(TAG, "sms   onFailure: "+ call);
                    }
                });

            }

            @Override
            public void fail() {
                alertDialog.dismiss();
            }
        });
    }


}
