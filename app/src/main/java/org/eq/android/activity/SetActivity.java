package org.eq.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import org.eq.android.R;
import org.eq.android.utils.MyUtil;


import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by liufan on 2019/6/13.
 */

public class SetActivity extends BaseActivity {

    @BindView(R.id.title_background)
    ImageView title_background;
    @BindView(R.id.button)
    RelativeLayout button;
    @BindView(R.id.relativelayout_geren)
    RelativeLayout relativelayout_geren;
    @BindView(R.id.relativelayout_reset)
    RelativeLayout relativelayout_reset;
    @BindView(R.id.relativelayout_back)
    RelativeLayout relativelayout_back;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_set;
    }

    @Override
    public void init(Bundle savedInstanceState) {

    }

    @OnClick({R.id.title_background, R.id.button, R.id.relativelayout_geren, R.id.relativelayout_reset, R.id.relativelayout_back})
    public void onclick(View view){
        switch (view.getId()){
            //关闭
            case R.id.title_background:
                finish();
                break;
            //退出登录
            case R.id.button:
                MyUtil.LoginOut(this);
                break;
            //个人资料
            case R.id.relativelayout_geren:
                startActivity(new Intent(this, PersonalDataActivity.class));
                break;
            //修改密码
            case R.id.relativelayout_reset:
                Intent intent =new Intent(this, ResetPasswordActivity.class);
                intent.putExtra("nameTpye","ChangePassword");
                startActivity(intent);
                break;
            //退出关闭应用
            case R.id.relativelayout_back:
                Toast.makeText(this,"退出关闭应用",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
