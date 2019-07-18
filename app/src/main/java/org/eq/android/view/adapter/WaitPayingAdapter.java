package org.eq.android.view.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import org.eq.android.R;
import org.eq.android.activity.BaseActivity;
import org.eq.android.entity.OrderTradeProcessing;
import org.eq.android.entity.OrderTradeWaitPaying;
import org.eq.android.entity.Product;
import org.eq.android.utils.MathUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WaitPayingAdapter extends BaseQuickAdapter<OrderTradeWaitPaying, BaseViewHolder> {

    BaseActivity activity;
    List<OrderTradeWaitPaying> data;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

    public WaitPayingAdapter(@Nullable List<OrderTradeWaitPaying> data, Activity activity) {
        super(R.layout.item_waitpaying, data);
        this.activity = (BaseActivity) activity;
        this.data = data;
        startTime();
    }

    @Override
    protected void convert(BaseViewHolder helper, OrderTradeWaitPaying item) {

        try {
            OrderTradeWaitPaying.Trade trade = item.getTrade();
            OrderTradeWaitPaying.OrderTradeUser orderTradeUser = item.getOrderTradeUser();

            if (!TextUtils.isEmpty(orderTradeUser.getPhoneHead())) {
                Glide.with(activity).load(orderTradeUser.getPhoneHead()).into((ImageView) helper.getView(R.id.iv_head));
            } else {
                Glide.with(activity).load(R.mipmap.ic_avatar).into((ImageView) helper.getView(R.id.iv_head));
            }

            long userId = activity.getUserId();
            if (userId != orderTradeUser.getSellUserId()) {
                if (!TextUtils.isEmpty(orderTradeUser.getSellUserNickName())) {
                    helper.setText(R.id.tv_name, orderTradeUser.getSellUserNickName());
                    helper.getView(R.id.tv_name).setVisibility(View.VISIBLE);
                } else {
                    helper.getView(R.id.tv_name).setVisibility(View.INVISIBLE);
                }
            } else {
                if (!TextUtils.isEmpty(orderTradeUser.getBuyUserNickName())) {
                    helper.setText(R.id.tv_name, orderTradeUser.getBuyUserNickName());
                    helper.getView(R.id.tv_name).setVisibility(View.VISIBLE);
                } else {
                    helper.getView(R.id.tv_name).setVisibility(View.INVISIBLE);
                }
            }

//            helper.getView(R.id.iv_vip); 是否实名认证

            if (item.getExpireTime() == null) {
                int payTimeOut = trade.getPayTimeOut();
                Date createDate = null;
                try {
                    createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(trade.getCreateTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                item.setExpireTime(payTimeOut * 60 * 60L - (int) ((System.currentTimeMillis() - createDate.getTime()) / 1000) + 16 * 60 * 60);
            }
            helper.setText(R.id.tv_pay_status, "支付倒计时 " + simpleDateFormat.format(new Date(item.getExpireTime() * 1000)));

            Glide.with(activity).load(trade.getProductImg()).into((ImageView) helper.getView(R.id.productPictureImageView));
            helper.setText(R.id.productNameTextView, trade.getProductName());
            helper.setText(R.id.productPriceTextView, MathUtils.fen2yuanStr(trade.getUnitPrice()));

            helper.setText(R.id.tv_unitprice, MathUtils.fen2yuanStr(trade.getSalePrice()));
            helper.setText(R.id.tv_sumtv, "共" + trade.getOrderNum() + "张");
            helper.setText(R.id.tv_qiugou, MathUtils.fen2yuanStr(trade.getAmount()));

            //去支付
            helper.addOnClickListener(R.id.button);
            //取消订单
            helper.addOnClickListener(R.id.button_cancel);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    /**
     * 列表倒计时
     */
    private void startTime() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < data.size(); i++) {
                            OrderTradeWaitPaying orderTradeWaitPaying = data.get(i);
                            long expireTime = Math.min(0, orderTradeWaitPaying.getExpireTime() - 1);
                            orderTradeWaitPaying.setExpireTime(expireTime);
                            WaitPayingAdapter.this.notifyItemChanged(i);
                        }

                    }
                });


            }
        }, 0, 1000);
    }

}
