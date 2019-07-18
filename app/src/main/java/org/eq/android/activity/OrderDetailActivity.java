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
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.eq.android.R;
import org.eq.android.api.ADOrderApi;
import org.eq.android.api.NetFactory;
import org.eq.android.api.OrderApi;
import org.eq.android.api.ResponseData;
import org.eq.android.common.Constant;
import org.eq.android.entity.ADOrder;
import org.eq.android.entity.OrderTrade;
import org.eq.android.entity.OrderTradeProcessing;
import org.eq.android.utils.DisplayUtil;
import org.eq.android.utils.MathUtils;
import org.eq.android.utils.StatusBarHeightUtil;
import org.eq.android.utils.SystemPreferences;
import org.eq.android.utils.ToastUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liufan on 2019/6/17.
 */
//订单详情
public class OrderDetailActivity extends BaseActivity {

    @BindView(R.id.title_background)
    ImageView title_background;

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
    @BindView(R.id.tv_complete_tpye)
    TextView tv_complete_tpye;
    @BindView(R.id.iv1)
    ImageView iv1;

    private String tradeNo, completeOrder;
    private int type;
    private String typetitle;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH时mm分ss秒");
    int time = 0;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            time--;
            Math.min(time, 0);
//            closeTimeTextView.setText(simpleDateFormat.format(new Date(time * 1000)));
            handler.postDelayed(this, 1000);
        }
    };


    @Override
    public int getContentViewResId() {
        return R.layout.activity_order_detail;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        makeStatusBarTransprant();
        initStatusBarHeight();
        DisplayUtil.expandTouchArea(iv_back);
        tradeNo = getIntent().getStringExtra("tradeNo");
        type = getIntent().getIntExtra("type",0);
        completeOrder = getIntent().getStringExtra("CompleteOrder");
        typetitle = getIntent().getStringExtra("typetitle");

        if (!TextUtils.isEmpty(completeOrder) && completeOrder.equals(completeOrder)){

            if (type==1 || type==2){
                getData1();
            }else if(type==3 || type==4){
                getData();
            }
        }else {
            if (!TextUtils.isEmpty(tradeNo)) {
                getData();
            }else {
                ToastUtil.showLongToast("订单号是空");
            }

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

    private void getData1(){
        NetFactory.create(ADOrderApi.class).getADOrder(tradeNo, (int) getUserId()).enqueue(new Callback<ResponseData<ADOrder>>() {
            @Override
            public void onResponse(Call<ResponseData<ADOrder>> call, Response<ResponseData<ADOrder>> response) {
                ResponseData<ADOrder> responseData = response.body();
                if (responseData != null && responseData.getStatus() == 200 && responseData.getData() != null) {
                    ADOrder adOrder = responseData.getData();
                    setAdOrderData(adOrder);
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

    private void initData(OrderTradeProcessing orderTradeProcessing) {

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


        tv_complete_tpye.setText(typetitle);

    }

    @OnClick({R.id.iv_back, R.id.eq_quest, R.id.button})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            //取消订单
            case R.id.eq_quest:
                cancelOrder();
                break;
            //立即支付
            case R.id.button:
                Intent intent = new Intent(OrderDetailActivity.this, PayOrderActivity.class);
                intent.putExtra("tradeNo", tradeNo);
                startActivity(intent);
                break;
        }
    }

    private void cancelOrder() {
        //取消订单
        NetFactory.create(OrderApi.class).cancelTradeOrder(tradeNo, getUserId()).enqueue(new Callback<ResponseData<OrderTrade>>() {
            @Override
            public void onResponse(Call<ResponseData<OrderTrade>> call, Response<ResponseData<OrderTrade>> response) {
                ResponseData<OrderTrade> responseData = response.body();
                if (responseData.getStatus() == 200) {
                    ToastUtil.showLongToast("订单已取消");
                    finish();
                } else {
                    ToastUtil.showLongToast(responseData.getErrMsg());
                }
            }

            @Override
            public void onFailure(Call<ResponseData<OrderTrade>> call, Throwable t) {
                ToastUtil.showLongToast("网络错误");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    private void setAdOrderData(ADOrder adOrder){
        tv_complete_tpye.setText(typetitle+"");
       /* int orderType =adOrder.getOrderType();
        if (orderType==1 || orderType==2 || orderType==5 || orderType==6){

//            iv1.setBackground();
        }else if(orderType==3 || orderType==7){
            tv_complete_tpye.setText("已取消");
            //iv1.setBackground();
        }else{
            tv_complete_tpye.setText("已关闭");
            //iv1.setBackground();
        }*/

        Glide.with(this).load(adOrder.getImg()).into(productPictureImageView);
        productNameTextView.setText(adOrder.getProductName());
        price2TextView.setText(MathUtils.fen2yuanStr(adOrder.getUnitPrice()));
        productStoreNameTextView.setText(adOrder.getAcceptName());

       /* String expirationDate = "有效期：";
        if (orderTradeProcessing.getProduct().getExpirationStart() == null || orderTradeProcessing.getProduct().getExpirationEnd() == null) {
            expirationDate = "永久有效";
        } else {
            expirationDate += orderTradeProcessing.getProduct().getExpirationStart().split(" ")[0].replaceAll("-", ".") + " - ";
            expirationDate += orderTradeProcessing.getProduct().getExpirationEnd().split(" ")[0].replaceAll("-", ".");
        }*/
        dateTextView.setText("");

//        Glide.with(this).load(adOrder.geta()).into(productLogImageView);
        tv_trade_sumprice.setText(Float.parseFloat(MathUtils.fen2yuanStr(adOrder.getPrice()))*adOrder.getOrderNumber() + "");

        tv_shangpinmingcheng.setText(adOrder.getProductName());
        tv_sell_name.setText(adOrder.getNickName());
        tv_trade_price.setText("¥ " + MathUtils.fen2yuanStr(adOrder.getPrice()));
        tv_trade_num.setText(adOrder.getOrderNumber() + "");
        tv_order_number.setText(adOrder.getOrderCode());
        tv_start_order.setText(adOrder.getCreateTime());

    }
}
