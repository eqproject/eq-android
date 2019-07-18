package org.eq.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import org.eq.android.R;
import org.eq.android.utils.BaseSystem;
import org.eq.android.utils.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by liufan on 2019/6/27.
 */

//设置信息类然后返回
public class SetEditTextActivity extends BaseActivity {

    @BindView(R.id.et_content)
    EditText et_content;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_set_edittext;
    }

    @Override
    public void init(Bundle savedInstanceState) {

        String content = getIntent().getStringExtra("NICKNAME");
        if (!TextUtils.isEmpty(content)){
            et_content.setText(content);
        }

    }

    @OnClick({R.id.save,R.id.iv_back})
    public void onclick(View view){
        switch (view.getId()){
            case R.id.save:
                String nickName = et_content.getText().toString().trim();
                if (!TextUtils.isEmpty(nickName)){
                    Intent intent =new Intent(this, PersonalDataActivity.class);
                    intent.putExtra("NICKNAME", nickName);
                    setResult(BaseSystem.NICKNAME, intent);
                    finish();
                }else{
                    ToastUtil.showLongToast("内容不能为空");
                }

                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
