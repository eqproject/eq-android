package org.eq.android.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.eq.android.R;
import org.eq.android.api.NetFactory;
import org.eq.android.api.ResponseData;
import org.eq.android.api.UserApi;
import org.eq.android.common.Constant;
import org.eq.android.utils.AESUtils;
import org.eq.android.utils.DisplayUtil;
import org.eq.android.utils.IdCardVerification;
import org.eq.android.utils.StatusBarHeightUtil;
import org.eq.android.utils.SystemPreferences;
import org.eq.android.utils.ToastUtil;

import java.text.ParseException;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liufan on 2019/6/23.
 */

public class UploadIDCardInformationActivity extends BaseActivity{
    @BindView(R.id.title_background)
    ImageView titleBackground;
    @BindView(R.id.barContainer)
    LinearLayout barContainer;
    @BindView(R.id.iv_back)
    ImageView iv_back;

    @BindView(R.id.tv3)
    AppCompatEditText tv3;
    @BindView(R.id.tv5)
    AppCompatEditText tv5;
    @BindView(R.id.button)
    RelativeLayout button;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_upload_id_cardinformation;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        makeStatusBarTransprant();
        initStatusBarHeight();
        DisplayUtil.expandTouchArea(iv_back);
    }

    private void makeStatusBarTransprant() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    //计算 statusbar 高度
    private void initStatusBarHeight() {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) titleBackground.getLayoutParams();
        layoutParams.height = StatusBarHeightUtil.subIOStatusBarHeight(this, layoutParams.height);
        titleBackground.setLayoutParams(layoutParams);

        ConstraintLayout.LayoutParams layoutParams1 = (ConstraintLayout.LayoutParams) barContainer.getLayoutParams();
        layoutParams1.topMargin = StatusBarHeightUtil.subIOStatusBarHeight(this, layoutParams1.topMargin);
        barContainer.setLayoutParams(layoutParams1);
    }


    @OnClick({R.id.iv_back, R.id.button})
    public void onclick(View view){
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.button:
                startIdVerification();
                break;

        }

    }

    private void startIdVerification(){
        String realName= tv3.getText().toString().trim();
        String IdNumber= tv5.getText().toString().trim();

        if (TextUtils.isEmpty(realName) || TextUtils.isEmpty(IdNumber)){
         ToastUtil.showShortToast("姓名，身份证号不能为空");
        }else{
            try {
                String idstring = IdCardVerification.IDCardValidate(IdNumber);
                if (idstring.equals("该身份证有效！")){
                    String idcard=AESUtils.encrypt(IdNumber, Constant.passwordKey);
                    NetFactory.create(UserApi.class).verify(SystemPreferences.getLong(Constant.USER_ID),realName, idcard).enqueue(new Callback<ResponseData>() {
                @Override
                public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                   int  code = response.body().getStatus();
                   if (code==200){
                       SystemPreferences.save(Constant.AUTHSTATUS,2);
                       ToastUtil.showLongToast("认证成功");
                       UploadIDCardInformationActivity.this.finish();
                       Log.e("...", "onResponse: "+response.body().getData() );
                   }else{
                       ToastUtil.showLongToast(response.body().getErrMsg());
                       Log.e("...", "onResponse: "+response.body().getStatus()+"..."+ response.body().getErrMsg());

                   }
                }

                @Override
                public void onFailure(Call<ResponseData> call, Throwable t) {

                }
            });
                }else{
                    ToastUtil.showShortToast(idstring);
                }
            }catch (ParseException e){
                e.printStackTrace();
            }


        }
    }
}
