package org.eq.android.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.eq.android.R;
import org.eq.android.api.NetFactory;
import org.eq.android.api.OrderApi;
import org.eq.android.api.ResponseData;
import org.eq.android.common.Constant;
import org.eq.android.entity.OrderTrade;
import org.eq.android.entity.OrderTradeProcessing;
import org.eq.android.utils.DisplayUtil;
import org.eq.android.utils.MathUtils;
import org.eq.android.utils.StatusBarHeightUtil;
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
 * 订单详情，进行中
 */
public class OrderDetailOngoingActivity extends BaseActivity {

    @BindView(R.id.title_background)
    ImageView title_background;
    @BindView(R.id.closeTimeTextView)
    TextView closeTimeTextView;

    @BindView(R.id.iv_back)
    ImageView iv_back;

    @BindView(R.id.scrollView)
    NestedScrollView scrollView;

    @BindView(R.id.topbackground) //券内容
            ImageView topbackground;

    @BindView(R.id.constrain_2) //券内容
            ConstraintLayout constrain_2;
    @BindView(R.id.productPictureImageView) //券内容
            AppCompatImageView productPictureImageView;
    @BindView(R.id.productNameTextView)
    AppCompatTextView productNameTextView;
    @BindView(R.id.price2TextView)
    AppCompatTextView price2TextView;
    @BindView(R.id.productLogImageView)
    AppCompatImageView productLogImageView;
    @BindView(R.id.productStoreNameTextView)
    AppCompatTextView productStoreNameTextView;
    @BindView(R.id.dateTextView)
    AppCompatTextView dateTextView;

    @BindView(R.id.tv_trade_sumprice) //交易总额
            TextView tv_trade_sumprice;
    @BindView(R.id.tv_shangpinmingcheng)
    TextView tv_shangpinmingcheng;
    @BindView(R.id.tv_sell_name)
    TextView tv_sell_name;
    @BindView(R.id.tv_trade_price)
    TextView tv_trade_price;
    @BindView(R.id.tv_trade_num)
    TextView tv_trade_num;
    @BindView(R.id.tv_order_number)
    TextView tv_order_number;
    @BindView(R.id.tv_start_order)
    TextView tv_start_order;
    @BindView(R.id.promptTextView)
    TextView promptTextView;
    @BindView(R.id.urgePay)
    TextView urgePay;

    private String tradeNo;
    private int sellUserId;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH时mm分ss秒");
    int time = 0;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            time--;
            Math.min(time, 0);
            closeTimeTextView.setText(simpleDateFormat.format(new Date(time * 1000)));
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public int getContentViewResId() {
        return R.layout.activity_order_detail_ongoing;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        makeStatusBarTransprant();
        initStatusBarHeight();
        DisplayUtil.expandTouchArea(iv_back);
        tradeNo = getIntent().getStringExtra("tradeNo");
        if (!TextUtils.isEmpty(tradeNo)) {
            getData();
        }

        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView nestedScrollView, int i, int i1, int i2, int i3) {
                if (i1 > 10) {
                    title_background.setBackgroundResource(R.color.color_F76646);
                } else if (i1 == 0) {
                    title_background.setBackgroundResource(android.R.color.transparent);
                }
            }
        });
    }

    /**
     * 状态栏覆盖掉
     */
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
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) title_background.getLayoutParams();
        layoutParams.height = StatusBarHeightUtil.subIOStatusBarHeight(this, layoutParams.height);
        title_background.setLayoutParams(layoutParams);

        ConstraintLayout.LayoutParams layoutParams2 = (ConstraintLayout.LayoutParams) topbackground.getLayoutParams();
        layoutParams2.height = StatusBarHeightUtil.subIOStatusBarHeight(this, layoutParams2.height);
        topbackground.setLayoutParams(layoutParams2);

        ConstraintLayout.LayoutParams layoutParams1 = (ConstraintLayout.LayoutParams) constrain_2.getLayoutParams();
        layoutParams1.topMargin = StatusBarHeightUtil.subIOStatusBarHeight(this, layoutParams1.topMargin);
        constrain_2.setLayoutParams(layoutParams1);
    }

    @OnClick({R.id.iv_back, R.id.urgePay})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            //催付款
            case R.id.urgePay:
                if (sellUserId==SystemPreferences.getLong(Constant.USER_ID)){
                    Intent intent = new Intent(OrderDetailOngoingActivity.this, PayOrderActivity.class);
                    intent.putExtra("tradeNo", tradeNo);
                    startActivity(intent);
                }else{
                    remindPay();
                }

                break;
        }
    }

    /**
     * 崔付款
     */
    private void remindPay() {
        NetFactory.create(OrderApi.class).remindPay(tradeNo).enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                ResponseData responseData = response.body();
                if (responseData.getStatus() == 200) {
                    makeButtonDisable();
                    ToastUtil.showLongToast("已催付");
                } else {
                    ToastUtil.showLongToast(responseData.getErrMsg());
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                ToastUtil.showLongToast("网络错误");
            }
        });
    }

    private void getData() {
        NetFactory.create(OrderApi.class).getDetails(tradeNo).enqueue(new Callback<ResponseData<OrderTradeProcessing>>() {
            @Override
            public void onResponse(Call<ResponseData<OrderTradeProcessing>> call, Response<ResponseData<OrderTradeProcessing>> response) {

                ResponseData<OrderTradeProcessing> responseData = response.body();
                if (responseData.getStatus() == 200) {
                    OrderTradeProcessing orderTradeProcessing = responseData.getData();
                    initData(orderTradeProcessing);
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

    private void initData(OrderTradeProcessing orderTradeProcessing) {

        sellUserId =orderTradeProcessing.getUser().getBuyUserId();

        if (sellUserId==SystemPreferences.getLong(Constant.USER_ID)){
            urgePay.setText("去支付");
        }

//        计算失效时间
        time = orderTradeProcessing.getTrade().getPayTimeOut() * 60 * 60;
        String createTimeStr = orderTradeProcessing.getTrade().getCreateTime();

        //计算倒计时
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date createDate = new Date();
        try {
            createDate = simpleDateFormat.parse(createTimeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        time = time - (int) ((System.currentTimeMillis() - createDate.getTime()) / 1000);
        time += 16 * 60 * 60;
        handler.postDelayed(runnable, 1000);


        Glide.with(this).load(orderTradeProcessing.getProduct().getImg()).into(productPictureImageView);
        productNameTextView.setText(orderTradeProcessing.getProduct().getProductName());
        price2TextView.setText(MathUtils.fen2yuanStr(orderTradeProcessing.getProduct().getUnitPrice()));
        productStoreNameTextView.setText(orderTradeProcessing.getProduct().getAcceptName());

        String expirationDate = "有效期：";
        if (orderTradeProcessing.getProduct().getExpirationStart() == null || orderTradeProcessing.getProduct().getExpirationEnd() == null) {
            expirationDate = "永久有效";
        } else {
            expirationDate += orderTradeProcessing.getProduct().getExpirationStart().split(" ")[0].replaceAll("-", ".") + " - ";
            expirationDate += orderTradeProcessing.getProduct().getExpirationEnd().split(" ")[0].replaceAll("-", ".");
        }
        dateTextView.setText(expirationDate);

        Glide.with(this).load(orderTradeProcessing.getProduct().getAcceptImg()).into(productLogImageView);
        tv_trade_sumprice.setText(MathUtils.fen2yuanStr(orderTradeProcessing.getTrade().getAmount()));

        tv_shangpinmingcheng.setText(orderTradeProcessing.getProduct().getProductName());
        tv_sell_name.setText(orderTradeProcessing.getUser().getBuyUserNickName());
        tv_trade_price.setText("¥ " + MathUtils.fen2yuanStr(orderTradeProcessing.getTrade().getSalePrice()));
        tv_trade_num.setText(orderTradeProcessing.getTrade().getOrderNum() + "");
        tv_order_number.setText(orderTradeProcessing.getTrade().getTradeNo());
        tv_start_order.setText(orderTradeProcessing.getTrade().getCreateTime());
        promptTextView.setText("等待收入买家金额：" + MathUtils.fen2yuanStr(orderTradeProcessing.getTrade().getAmount()) + "元，" +
                "平台收取交易服务费：" + MathUtils.fen2yuanStr(orderTradeProcessing.getTrade().getServiceFee()) + "元");

        if (orderTradeProcessing.getTrade().getRemindPay() == 1) {
            makeButtonDisable();
        }
    }

    private void makeButtonDisable() {
        urgePay.setText("已催");
        urgePay.setBackgroundResource(R.color.color_E2E2E2);
        urgePay.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
