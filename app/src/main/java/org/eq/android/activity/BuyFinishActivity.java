package org.eq.android.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.eq.android.R;
import org.eq.android.api.NetFactory;
import org.eq.android.api.OrderApi;
import org.eq.android.api.ResponseData;
import org.eq.android.entity.OrderTrade;
import org.eq.android.entity.OrderTradeProcessing;
import org.eq.android.entity.Product;
import org.eq.android.entity.User;
import org.eq.android.utils.MathUtils;
import org.eq.android.utils.StatusBarHeightUtil;
import org.eq.android.utils.ToastUtil;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 购买完成
 */
public class BuyFinishActivity extends BaseActivity {


    @BindView(R.id.title_background)
    ImageView titleBackground;
    @BindView(R.id.barContainer)
    LinearLayout barContainer;

    @BindView(R.id.totalPriceTextView)
    TextView totalPriceTextView;
    @BindView(R.id.totalPrice2TextView)
    TextView totalPrice2TextView;
    @BindView(R.id.priceTextView)
    TextView priceTextView;
    @BindView(R.id.numTextView)
    TextView numTextView;

    @BindView(R.id.productPictureImageView)
    ImageView productPictureImageView;
    @BindView(R.id.productNameTextView)
    TextView productNameTextView;
    @BindView(R.id.price2TextView)
    TextView price2TextView;
    @BindView(R.id.productLogImageView)
    ImageView productLogImageView;
    @BindView(R.id.productStoreNameTextView)
    TextView productStoreNameTextView;
    @BindView(R.id.dateTextView)
    TextView dateTextView;


    @BindView(R.id.sellUserNameTextView)
    TextView sellUserNameTextView;
    @BindView(R.id.buyUserNameTextView)
    TextView buyUserNameTextView;
    @BindView(R.id.orderNumTextView)
    TextView orderNumTextView;
    @BindView(R.id.alipayNumTextView)
    TextView alipayNumTextView;
    @BindView(R.id.payDateTimeTextView)
    TextView payDateTimeTextView;


    Product product;
    User user;
    OrderTrade orderTrade;


    @Override
    public int getContentViewResId() {
        return R.layout.activity_buy_finish;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        makeStatusBarTransprant();
        initStatusBarHeight();
        String tradeNo = getIntent().getStringExtra("tradeNo");
        initData(tradeNo);
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


    private void initData(String tradeNo) {
        //请求订单详情
        NetFactory.create(OrderApi.class).getDetails(tradeNo).enqueue(new Callback<ResponseData<OrderTradeProcessing>>() {
            @Override
            public void onResponse(Call<ResponseData<OrderTradeProcessing>> call, Response<ResponseData<OrderTradeProcessing>> response) {
                ResponseData<OrderTradeProcessing> responseData = response.body();
                if (responseData != null && responseData.getStatus() == 200 && responseData.getData() != null) {
                    OrderTradeProcessing orderTradeProcessing = responseData.getData();
                    product = orderTradeProcessing.getProduct();
                    user = orderTradeProcessing.getUser();
                    orderTrade = orderTradeProcessing.getTrade();
                    setData();
                } else {
                    ToastUtil.showLongToast(responseData.getErrMsg());
                }
            }

            @Override
            public void onFailure(Call<ResponseData<OrderTradeProcessing>> call, Throwable t) {
                ToastUtil.showLongToast("网络错误");
            }
        });
    }

    private void setData() {

        totalPriceTextView.setText(MathUtils.fen2yuanStr(orderTrade.getAmount()));
        totalPrice2TextView.setText("+" + MathUtils.fen2yuanStr(orderTrade.getAmount()) + "元");
        priceTextView.setText("+" + MathUtils.fen2yuanStr(orderTrade.getSalePrice()) + "元");
        numTextView.setText(orderTrade.getOrderNum() + "张");

        Glide.with(this)
                .load(product.getImg())
                .into(productPictureImageView);
        productNameTextView.setText(product.getProductName());
        price2TextView.setText(MathUtils.fen2yuanStr(product.getUnitPrice()));
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


        sellUserNameTextView.setText(user.getSellUserNickName());
        buyUserNameTextView.setText(user.getBuyUserNickName());
        orderNumTextView.setText(orderTrade.getTradeNo());
        alipayNumTextView.setText(orderTrade.getPayNo());
        payDateTimeTextView.setText(orderTrade.getPayTime());

    }

    @OnClick({R.id.iv_back, R.id.returnHome})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.returnHome:
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }
}
