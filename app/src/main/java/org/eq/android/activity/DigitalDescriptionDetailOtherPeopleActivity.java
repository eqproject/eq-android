package org.eq.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.githang.statusbar.StatusBarCompat;

import org.eq.android.R;
import org.eq.android.api.NetFactory;
import org.eq.android.api.ProductApi;
import org.eq.android.api.ResponseData;
import org.eq.android.entity.Product;
import org.eq.android.utils.DisplayUtil;
import org.eq.android.utils.MathUtils;
import org.eq.android.utils.MyUtil;
import org.eq.android.utils.ToastUtil;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 不知道自己有没有数字券详情，来自我要买广告订单
 */
public class DigitalDescriptionDetailOtherPeopleActivity extends BaseActivity {

    @BindView(R.id.productPictureImageView)
    ImageView productPictureImageView;
    @BindView(R.id.productNameTextView)
    TextView productNameTextView;
    @BindView(R.id.priceTextView)
    TextView priceTextView;
    @BindView(R.id.productLogImageView)
    ImageView productLogImageView;
    @BindView(R.id.productStoreNameTextView)
    TextView productStoreNameTextView;
    @BindView(R.id.dateTextView)
    TextView dateTextView;

    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.tv_content)
    TextView tv_content;

    @BindView(R.id.productProviderTextView)
    TextView productProviderTextView;

    @BindView(R.id.ivcorn)
    ImageView ivcorn;
    @BindView(R.id.ivContent)
    TextView ivContent;

    @BindView(R.id.chengduishuoming_content)
    TextView chengduishuoming_content;

    @BindView(R.id.falvzhichi_content)
    TextView falvzhichi_content;

    @BindView(R.id.tv_sell)
    Button tv_sell;

    @BindView(R.id.iv_back)
    ImageView iv_back;


    Product product;
    String orderCode;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_digital_description_detail_other_people;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.colorwhite), true);
        DisplayUtil.expandTouchArea(iv_back);
        int productId = getIntent().getIntExtra("productId", 0);
        orderCode = getIntent().getStringExtra("orderCode");
        if (orderCode == null) {
            tv_sell.setVisibility(View.GONE);
        }
        initData(productId);
    }

    private void initData(int productId) {
        NetFactory.create(ProductApi.class).getPlatformDetails((int) getUserId(), productId).enqueue(new Callback<ResponseData<Product>>() {
            @Override
            public void onResponse(Call<ResponseData<Product>> call, Response<ResponseData<Product>> response) {
                ResponseData<Product> responseData = response.body();
                if (responseData != null && responseData.getStatus() == 200 && responseData.getData() != null) {
                    product = responseData.getData();
                    initView();
                } else {
                    ToastUtil.showLongToast(responseData.getErrMsg());
                }

            }

            @Override
            public void onFailure(Call<ResponseData<Product>> call, Throwable t) {
                ToastUtil.showLongToast("网络错误");
            }
        });
    }

    private void initView() {
        Glide.with(this)
                .load(product.getImg())
                .into(productPictureImageView);
        productNameTextView.setText(product.getProductName());
        priceTextView.setText(MathUtils.fen2yuanStr(product.getUnitPrice()));
        Glide.with(this)
                .load(product.getIssuerImg())
                .into(productLogImageView);
        productStoreNameTextView.setText(product.getAcceptName());


        String expirationDate = "";
        if (product.getExpirationStart() == null || product.getExpirationEnd() == null) {
            expirationDate = "永久有效";
        } else {
            expirationDate += product.getExpirationStart().split(" ")[0].replaceAll("-", ".") + " - ";
            expirationDate += product.getExpirationEnd().split(" ")[0].replaceAll("-", ".");
        }
        dateTextView.setText(expirationDate);


        tv_name.setText(product.getAcceptName());
        tv_content.setText(product.getAcceptIntro());

        Glide.with(this)
                .load(product.getIssuerImg())
                .into(ivcorn);
        ivContent.setText(product.getIssuerIntro());

        chengduishuoming_content.setText(product.getReceive());

        String text = "·本券在承兑过程中出现问题，由 " + "<font color='#F76646'>法律机构名称</font>" + " 提供法律咨询服务";
        falvzhichi_content.setText(Html.fromHtml(text));
    }

    @OnClick({R.id.tv_sell, R.id.iv_back})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.tv_sell:
                if (MyUtil.isLoginDeal(this)) {
                    Intent intent = new Intent(this, BuySettingActivity.class);
                    intent.putExtra("orderCode", orderCode);
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_bottom_enter, 0);
                }
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
