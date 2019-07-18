package org.eq.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.githang.statusbar.StatusBarCompat;

import org.eq.android.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 发布信息
 * Created by liufan on 2019/6/5.
 */

public class ReleaseActivity extends BaseActivity {

    @BindView(R.id.button_sell)
    RelativeLayout button_sell;
    @BindView(R.id.button_bug)
    RelativeLayout button_bug;
    @BindView(R.id.iv_back)
    ImageView iv_back;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_release;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.colorwhite), true);

    }

    @OnClick({R.id.button_sell, R.id.button_bug, R.id.iv_back})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.button_sell:
                startActivity(new Intent(this, SellProductListActivity.class));
                break;
            case R.id.button_bug:
                startActivity(new Intent(this, BuyProductListActivity.class));
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
