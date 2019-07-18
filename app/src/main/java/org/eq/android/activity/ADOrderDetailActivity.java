package org.eq.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.githang.statusbar.StatusBarCompat;
import com.orhanobut.logger.Logger;

import org.eq.android.R;
import org.eq.android.api.ADOrderApi;
import org.eq.android.api.NetFactory;
import org.eq.android.api.OrderApi;
import org.eq.android.api.ProductApi;
import org.eq.android.api.ResponseData;
import org.eq.android.common.Constant;
import org.eq.android.entity.ADOrder;
import org.eq.android.entity.OrderTradeProcessing;
import org.eq.android.entity.Product;
import org.eq.android.utils.DisplayUtil;
import org.eq.android.utils.MathUtils;
import org.eq.android.utils.MyUtil;
import org.eq.android.utils.SystemPreferences;
import org.eq.android.utils.ToastUtil;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 出售广告详情
 */
public class ADOrderDetailActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView iv_back;

    @BindView(R.id.avatarImageView)
    ImageView avatarImageView;
    @BindView(R.id.nameTextView)
    TextView nameTextView;
    @BindView(R.id.vipImageView)
    ImageView vipImageView;
    @BindView(R.id.authImageView)
    ImageView authImageView;
    @BindView(R.id.releaseTimeTextView)
    TextView releaseTimeTextView;
    @BindView(R.id.salePriceTextView)
    TextView salePriceTextView;
    @BindView(R.id.saleNumberTextView)
    TextView saleNumberTextView;
    @BindView(R.id.orderTitleTextView)
    TextView orderTitleTextView;

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

    @BindView(R.id.nameTextView2)
    TextView nameTextView2;
    @BindView(R.id.saleedNumberTextView)
    TextView saleedNumberTextView;

    ADOrder adOrder;
    Product product;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_adorder_detail;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.colorwhite), true);
        String orderCode = getIntent().getStringExtra("orderCode");
        DisplayUtil.expandTouchArea(iv_back);
        initData(orderCode);
    }

    //请求订单详情
    private void initData(String orderCode) {
        NetFactory.create(ADOrderApi.class).getADOrder(orderCode, (int) getUserId()).enqueue(new Callback<ResponseData<ADOrder>>() {
            @Override
            public void onResponse(Call<ResponseData<ADOrder>> call, Response<ResponseData<ADOrder>> response) {
                ResponseData<ADOrder> responseData = response.body();
                if (responseData != null && responseData.getStatus() == 200 && responseData.getData() != null) {
                    adOrder = responseData.getData();
                    initProduct();
                    setData();
                } else {
                    ToastUtil.showLongToast(responseData.getErrMsg());
                }
            }

            @Override
            public void onFailure(Call<ResponseData<ADOrder>> call, Throwable t) {
                ToastUtil.showLongToast("网络错误");

            }
        });
    }

    //设置数据
    private void setData() {
        Glide.with(this)
                .load(adOrder.getUserImg())
                .into(avatarImageView);
        nameTextView.setText(adOrder.getNickName());
        salePriceTextView.setText(MathUtils.fen2yuanStr(adOrder.getPrice()));
        saleNumberTextView.setText("待售数量 " + adOrder.getSaleNumber());
        orderTitleTextView.setText(adOrder.getTitle());

        nameTextView2.setText(adOrder.getNickName());
        saleedNumberTextView.setText("交易数 " + adOrder.getTradeNum());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            long time = System.currentTimeMillis() - simpleDateFormat.parse(adOrder.getCreateTime()).getTime();
            releaseTimeTextView.setText("发布于" + formatTime(time) + "前");

        } catch (ParseException e) {
            e.printStackTrace();
            releaseTimeTextView.setVisibility(View.INVISIBLE);
        }
    }

    private String formatTime(long time) throws ParseException {
        if (time < 0) throw new ParseException("时间差负", 0);
        time = time / 1000;
        if (time > 12 * 30 * 24 * 60 * 60)
            return (time / (12 * 30 * 24 * 60 * 60)) + "年";
        else if (time > 30 * 24 * 60 * 60)
            return (time / (30 * 24 * 60 * 60)) + "月";
        else if (time > 24 * 60 * 60)
            return (time / (24 * 60 * 60)) + "天";
        else if (time > 60 * 60)
            return (time / (60 * 60)) + "小时";
        else if (time > 60)
            return (time / 60) + "分";
        else
            return time + "秒";
    }

    //请求卡券详情，订单中没有
    private void initProduct() {
        int productId = adOrder.getProductId();
        NetFactory.create(ProductApi.class).getPlatformDetails((int) getUserId(), productId).enqueue(new Callback<ResponseData<Product>>() {
            @Override
            public void onResponse(Call<ResponseData<Product>> call, Response<ResponseData<Product>> response) {
                ResponseData<Product> responseData = response.body();
                if (responseData != null && responseData.getStatus() == 200 && responseData.getData() != null) {
                    product = responseData.getData();
                    initProductView();
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

    //初始化 卡券 那些 view
    private void initProductView() {
        Glide.with(this)
                .load(product.getImg())
                .into(productPictureImageView);
        productNameTextView.setText(product.getProductName());
        priceTextView.setText(new BigDecimal(product.getUnitPrice()).divide(new BigDecimal(100)).toString());
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


    }

    @OnClick({R.id.iv_back, R.id.iv_share, R.id.buyButton, R.id.supportTermsLinearLayout, R.id.productDetailLinearLayout})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_share:
                //todo 分享
                break;
            case R.id.buyButton:
                if (MyUtil.isLoginDeal(this)) {
                    if (SystemPreferences.getInt(Constant.AUTHSTATUS)==2){
                        Intent intent = new Intent(this, BuySettingActivity.class);
                        intent.putExtra("orderCode", adOrder.getOrderCode());
                        startActivity(intent);
                        overridePendingTransition(R.anim.activity_bottom_enter, 0);
                    }else{
                        startActivity(new Intent(this, UploadIDCardInformationActivity.class));
                    }

                }
                break;
            case R.id.supportTermsLinearLayout:
                //todo 平台服务
                break;
            case R.id.productDetailLinearLayout:
                Intent intent1 = new Intent(this, DigitalDescriptionDetailOtherPeopleActivity.class);
                intent1.putExtra("productId", product.getId());
                intent1.putExtra("orderCode", adOrder.getOrderCode());
                startActivity(intent1);
                break;
        }
    }
}
