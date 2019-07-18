package org.eq.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.eq.android.R;
import org.eq.android.view.FocusPhoneCode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by liufan on 2019/6/2.
 */

public class SetPasswordActivity extends BaseActivity{

    @BindView(R.id.constrain_set_password)
    ConstraintLayout constrain_set_password;
    @BindView(R.id.constrain_getver_code)
    ConstraintLayout constrain_getver_code;
    @BindView(R.id.input_code_textView4)
    TextView input_code_textView4;
    @BindView(R.id.input_code_fpcode)
    FocusPhoneCode input_code_fpcode;
    @BindView(R.id.input_code_button)
    RelativeLayout input_code_button;
    @BindView(R.id.set_pd_text_input_et_EditText)
    EditText set_pd_text_input_et_EditText;
    @BindView(R.id.set_pd_again_input_et_EditText)
    EditText set_pd_again_input_et_EditText;
    @BindView(R.id.set_pd_again_button)
    RelativeLayout set_pd_again_button;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_setpasswrod;
    }

    @Override
    public void init(Bundle savedInstanceState) {

    }

    @OnClick({R.id.input_code_button, R.id.set_pd_again_button})
    public void onclick(View view){
        switch (view.getId()){
            case R.id.input_code_button:
                constrain_getver_code.setVisibility(View.GONE);
                constrain_set_password.setVisibility(View.VISIBLE);
                break;
            case R.id.set_pd_again_button:
                String password = set_pd_text_input_et_EditText.getText().toString().trim();
                String againPasswrod = set_pd_again_input_et_EditText.getText().toString().trim();
                if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(againPasswrod)){
                    if (password.equals(againPasswrod)){
                        startActivity(new Intent(this, MainActivity.class));
                    }
                }else{
                    Toast.makeText(this, "密码不能为空，且要两次设置要一样", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

}
