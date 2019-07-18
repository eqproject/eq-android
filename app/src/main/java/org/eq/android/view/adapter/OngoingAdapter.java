package org.eq.android.view.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import org.eq.android.R;
import org.eq.android.entity.OrderTradeProcessing;
import org.eq.android.entity.OrderTradeProcessingNew;
import org.eq.android.entity.Product;
import org.eq.android.utils.MathUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 进行中订单 adapter
 */
public class OngoingAdapter extends BaseQuickAdapter<OrderTradeProcessingNew, BaseViewHolder> {

    Activity activity;

    public OngoingAdapter(@Nullable List<OrderTradeProcessingNew> data, Activity activity) {
        super(R.layout.item_ongoing, data);
        this.activity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, OrderTradeProcessingNew item) {

        try {

            if (!TextUtils.isEmpty(item.getPhotoHead())) {
                Glide.with(activity).load(item.getPhotoHead()).into((ImageView) helper.getView(R.id.iv_head));
            } else {
                Glide.with(activity).load(R.mipmap.ic_avatar).into((ImageView) helper.getView(R.id.iv_head));
            }

            helper.setText(R.id.tv_name, item.getUserNickName()); //用户名
            //            helper.getView(R.id.iv_vip); 是否实名认证

            helper.setText(R.id.tv_pay_status, item.getStateRemark());

            helper.setText(R.id.productNameTextView, item.getProductName());
            helper.setText(R.id.productPriceTextView, "￥" + MathUtils.fen2yuanStr(item.getUnitPrice()));
            helper.setText(R.id.tv_sum, "共" + item.getOrderNum() + "张");
            helper.setText(R.id.tv_hejiprice, MathUtils.fen2yuanStr(item.getAmount()));

            Glide.with(activity).load(item.getProductImg()).into((ImageView) helper.getView(R.id.productPictureImageView));
            helper.addOnClickListener(R.id.button);

            View button = helper.getView(R.id.button);
            TextView tv_ongoing_pay = helper.getView(R.id.tv_ongoing_pay);
            int status = item.getStatus();

            if (item.getAllAppeal() == 1) {
                button.setVisibility(View.VISIBLE);
                tv_ongoing_pay.setVisibility(View.VISIBLE);
                button.setBackgroundResource(R.drawable.ellipse_red);
                tv_ongoing_pay.setText("申诉");
                tv_ongoing_pay.setTextColor(activity.getResources().getColor(R.color.color_F76646));
            } else if ((status == 3 || status == 5) && item.getRemindPay() == 0) {
                button.setVisibility(View.VISIBLE);
                tv_ongoing_pay.setVisibility(View.VISIBLE);
                button.setBackgroundResource(R.drawable.ellipse_red);
                tv_ongoing_pay.setText("催TA付款");
                tv_ongoing_pay.setTextColor(activity.getResources().getColor(R.color.color_F76646));
            } else if (item.getRemindPay() == 1) {
                button.setVisibility(View.VISIBLE);
                tv_ongoing_pay.setVisibility(View.VISIBLE);
                button.setBackgroundResource(R.drawable.ellipse_gray);
                tv_ongoing_pay.setText("已催");
                tv_ongoing_pay.setTextColor(activity.getResources().getColor(R.color.color_B1B0B0));
            } else {
                button.setVisibility(View.INVISIBLE);
                tv_ongoing_pay.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
