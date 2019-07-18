package org.eq.android.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;

import org.eq.android.R;
import org.eq.android.api.NetFactory;
import org.eq.android.api.OrderApi;
import org.eq.android.api.ResponseData;
import org.eq.android.entity.OrderTrade;
import org.eq.android.entity.OrderTradeProcessing;
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
 * 完成出售
 */
public class SellFinishActivity extends BaseActivity {

    @BindView(R.id.lastTimeTextView)
    TextView lastTimeTextView;

    OrderTradeProcessing orderTradeProcessing;


    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
    int time = (24 + 16) * 60 * 60;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            time--;
            Math.min(time, 0);
            lastTimeTextView.setText("出售剩余时间 " + simpleDateFormat.format(new Date(time * 1000)));
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public int getContentViewResId() {
        return R.layout.activity_sell_finish;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        makeStatusBarTransprant();
        handler.postDelayed(runnable, 1000);
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

    private void initData(String tradeNo) {
        //请求订单详情
        NetFactory.create(OrderApi.class).getDetails(tradeNo).enqueue(new Callback<ResponseData<OrderTradeProcessing>>() {
            @Override
            public void onResponse(Call<ResponseData<OrderTradeProcessing>> call, Response<ResponseData<OrderTradeProcessing>> response) {
                ResponseData<OrderTradeProcessing> responseData = response.body();
                if (responseData != null && responseData.getStatus() == 200 && responseData.getData() != null) {
                    orderTradeProcessing = responseData.getData();
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
        //计算倒计时
        OrderTrade orderTrade = orderTradeProcessing.getTrade();

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

    @OnClick({R.id.closeImageView, R.id.returnHome, R.id.remindThemTextView})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.closeImageView:
                finish();
                break;
            case R.id.returnHome:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.remindThemTextView:
                //todo 提醒求购者
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
