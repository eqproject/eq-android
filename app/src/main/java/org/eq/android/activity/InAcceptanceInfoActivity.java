package org.eq.android.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.eq.android.R;
import org.eq.android.api.EntityList;
import org.eq.android.api.NetFactory;
import org.eq.android.api.ResponseData;
import org.eq.android.api.VoucherApi;
import org.eq.android.entity.AcceptDetail;
import org.eq.android.entity.OverDue;
import org.eq.android.utils.DisplayUtil;
import org.eq.android.utils.StatusBarHeightUtil;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liufan on 2019/6/11.
 */

//承兑中详情
public class InAcceptanceInfoActivity extends BaseActivity {

    @BindView(R.id.title_background)
    ImageView titleBackground;
    @BindView(R.id.barContainer)
    LinearLayout barContainer;
    @BindView(R.id.iv_back)
    ImageView iv_back;

    @BindView(R.id.tv_backhome)
    TextView tv_backhome;
    @BindView(R.id.productPictureImageView)
    AppCompatImageView  productPictureImageView;
    @BindView(R.id.productNameTextView)
    AppCompatTextView productNameTextView;
    @BindView(R.id.productNumberTextView)
    AppCompatTextView productNumberTextView;
    @BindView(R.id.tv_number)
    TextView tv_number;
    @BindView(R.id.contact_name)
    TextView contact_name;
    @BindView(R.id.contact_number)
    TextView contact_number;
    @BindView(R.id.address)
    TextView address;
    @BindView(R.id.legal_support)
    TextView legal_support;
    @BindView(R.id.onsultants)
    TextView onsultants;
    @BindView(R.id.tv_status_accept)
    TextView tv_status_accept;
    @BindView(R.id.im_status_background)
    ImageView im_status_background;
    @BindView(R.id.fawuandzixun)
    ConstraintLayout fawuandzixun;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_in_acceptance_info;
    }

    @Override
    public void init(Bundle savedInstanceState) {

        makeStatusBarTransprant();
        initStatusBarHeight();
        DisplayUtil.expandTouchArea(iv_back);

        fawuandzixun.setVisibility(View.GONE);
        String id = getIntent().getStringExtra("id");

//        id="AS20190605020737853317";
        if (!TextUtils.isEmpty(id)){
            NetFactory.create(VoucherApi.class).getAcceptDetail((int)getUserId(), id).enqueue(new Callback<ResponseData<AcceptDetail>>() {
                @Override
                public void onResponse(Call<ResponseData<AcceptDetail>> call, Response<ResponseData<AcceptDetail>> response) {
                    ResponseData<AcceptDetail> result =response.body();
                    if (result.getStatus() == 200){
                        AcceptDetail acceptDetail1 =result.getData();
                        if (acceptDetail1 !=null){
                            initData(acceptDetail1);
                        }else{
                            Log.e("...", "onResponse: " );
                        }
                    }

                }

                @Override
                public void onFailure(Call<ResponseData<AcceptDetail>> call, Throwable t) {

                }
            });
        }

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

    private void initData(AcceptDetail acceptDetail1){

        Picasso.get().load(acceptDetail1.getImg()).into(productPictureImageView);
        productNameTextView.setText(acceptDetail1.getProductName());
        productNumberTextView.setText(acceptDetail1.getUnitPrice()+"");
        tv_number.setText(acceptDetail1.getNumber()+"");
        contact_name.setText(acceptDetail1.getConsignee());
        contact_number.setText(acceptDetail1.getConsigneePhone());
        address.setText(acceptDetail1.getConsigneeAddress());

        if (acceptDetail1.getStateRemak().equals("等待承兑")){
            tv_status_accept.setText("正在等待商家承兑...");
            Picasso.get().load(R.mipmap.eq_truck).into(im_status_background);
        }else{
            tv_status_accept.setText("承兑申请成功");
            Picasso.get().load(R.mipmap.ic_success).into(im_status_background);
        }

    }

    @OnClick({R.id.tv_backhome,R.id.iv_back,R.id.legal_support,R.id.onsultants})
    public void onclick(View view){
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            //返回首页
            case R.id.tv_backhome:
                finish();
                break;
            //法务
            case R.id.legal_support:
                break;
            //咨询商家
            case R.id.onsultants:
                break;
        }
    }
}
