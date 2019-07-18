package org.eq.android.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;

import org.eq.android.R;
import org.eq.android.api.NetFactory;
import org.eq.android.api.OrderApi;
import org.eq.android.api.ProductApi;
import org.eq.android.api.ResponseData;
import org.eq.android.common.Constant;
import org.eq.android.entity.OrderTrade;
import org.eq.android.entity.OrderTradeProcessing;
import org.eq.android.entity.Product;
import org.eq.android.entity.User;
import org.eq.android.utils.DisplayUtil;
import org.eq.android.utils.MathUtils;
import org.eq.android.utils.StatusBarHeightUtil;
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
 * Created by liufan on 2019/6/6.
 * 支付选择
 */

public class PayOrderActivity extends BaseActivity {

    @BindView(R.id.title_background)
    ImageView titleBackground;
    @BindView(R.id.barContainer)
    LinearLayout barContainer;
    @BindView(R.id.iv_back)
    ImageView iv_back;

    @BindView(R.id.priceTextView)
    TextView priceTextView;
    @BindView(R.id.productNameTextView)
    TextView productNameTextView;
    @BindView(R.id.countdown)
    TextView countdown;


    @BindView(R.id.select_zhifubao)
    ConstraintLayout select_zhifubao;
    @BindView(R.id.select_weixin)
    ConstraintLayout select_weixin;
    @BindView(R.id.iv_select_weixin)
    ImageView iv_select_weixin;
    @BindView(R.id.iv_select_zhifubao)
    ImageView iv_select_zhifubao;

    @BindView(R.id.button)
    RelativeLayout button;

    Product product;
    User user;
    OrderTrade orderTrade;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
    int time = 0;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            time--;
            Math.min(time, 0);
            countdown.setText("支付剩余时间 " + simpleDateFormat.format(new Date(time * 1000)));
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public int getContentViewResId() {
        return R.layout.activity_pay_order;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        makeStatusBarTransprant();
        initStatusBarHeight();
        DisplayUtil.expandTouchArea(iv_back);
        Intent intent = getIntent();
        String tradeNo = intent.getStringExtra("tradeNo");
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
        priceTextView.setText(MathUtils.fen2yuanStr(orderTrade.getAmount()));
        productNameTextView.setText(product.getProductName() + " x " + orderTrade.getOrderNum() + "张");

        //计算倒计时
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date createDate = new Date();
        try {
            createDate = simpleDateFormat.parse(orderTrade.getCreateTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int payTimeOut = orderTrade.getPayTimeOut();
        time = payTimeOut * 60 * 60 - (int) ((System.currentTimeMillis() - createDate.getTime()) / 1000);
        time += 16 * 60 * 60;
        handler.postDelayed(runnable, 1000);

    }

    @OnClick({R.id.iv_back, R.id.select_zhifubao, R.id.select_weixin, R.id.button})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.button:

                String tradeNo = orderTrade.getTradeNo();

                NetFactory.create(OrderApi.class).prePay(tradeNo, (int) getUserId()).enqueue(new Callback<ResponseData>() {
                    @Override
                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                        ResponseData responseData = response.body();
                        if (responseData != null && responseData.getStatus() == 200) {
                            //验证订单符合支付状态，开始支付
                            paySuccess();
                        } else {
                            ToastUtil.showLongToast(responseData.getErrMsg());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseData> call, Throwable t) {
                        ToastUtil.showLongToast("网络错误");
                    }
                });


                break;
            case R.id.select_zhifubao:
                iv_select_weixin.setBackgroundResource(R.mipmap.eq_noselect);
                iv_select_zhifubao.setBackgroundResource(R.mipmap.eq_select);
                break;
            case R.id.select_weixin:
                iv_select_weixin.setBackgroundResource(R.mipmap.eq_select);
                iv_select_zhifubao.setBackgroundResource(R.mipmap.eq_noselect);
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void paySuccess() {
        //todo 这些应该是支付后拿到的
        String tradeNo = orderTrade.getTradeNo();
        String payNo = tradeNo + "pay";
        int payAmout = orderTrade.getAmount();
        int payType = 1; //支付方式1：支付宝；2：微信
        int payStatus = 1; //支付状态1：成功；2：失败

        NetFactory.create(OrderApi.class).payNotify(tradeNo, payNo, payAmout, payType, payStatus).enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                ResponseData responseData = response.body();
                if (responseData != null && responseData.getStatus() == 200) {

                    Intent intentBroadcast=new Intent();
                    intentBroadcast.putExtra("type",1);
                    sendBroadcast(intentBroadcast);

                    Intent intent = new Intent(PayOrderActivity.this, BuyFinishActivity.class);
                    intent.putExtra("tradeNo", orderTrade.getTradeNo());
                    startActivity(intent);
                    finish();

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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
