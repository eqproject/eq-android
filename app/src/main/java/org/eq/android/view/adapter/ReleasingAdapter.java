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
import org.eq.android.entity.OrderTradeProcessing;
import org.eq.android.entity.OrderTradeProcessingNew;
import org.eq.android.entity.Product;
import org.eq.android.utils.MathUtils;

import java.util.List;

public class ReleasingAdapter extends BaseQuickAdapter<OrderTradeProcessingNew, BaseViewHolder> {

    Activity activity;

    public ReleasingAdapter(@Nullable List<OrderTradeProcessingNew> data, Activity activity) {
        super(R.layout.item_releasing_order, data);
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

            if (!TextUtils.isEmpty(item.getNickName())) {
                helper.setText(R.id.tv_name, item.getNickName());
                helper.getView(R.id.tv_name).setVisibility(View.VISIBLE);
            } else {
                helper.getView(R.id.tv_name).setVisibility(View.INVISIBLE);
            }

//            helper.getView(R.id.iv_vip); 是否实名认证
            helper.setText(R.id.tv_pay_status, item.getStateRemark());
            Glide.with(activity).load(item.getProductImg()).into((ImageView) helper.getView(R.id.productPictureImageView));
            helper.setText(R.id.productNameTextView, item.getProductName());
            helper.setText(R.id.productPriceTextView, MathUtils.fen2yuanStr(item.getUnitPrice()));

            //求购
            if (item.getOrderType() == 2) {
                helper.setText(R.id.orderNumber, "求购数量：" + item.getOrderNumber());
                helper.setText(R.id.saleedNumber, "已购数量：" + item.getSaleedNumber());
                helper.setText(R.id.saleNumber, "待购数量：" + item.getSaleNumber());
                helper.setTextColor(R.id.saleNumber, activity.getResources().getColor(R.color.color_727BD5));
                helper.setTextColor(R.id.tv_unitprice, activity.getResources().getColor(R.color.color_727BD5));
                helper.setTextColor(R.id.tv_qiugou_unitprice, activity.getResources().getColor(R.color.color_727BD5));
                helper.setText(R.id.tv_qiugou, MathUtils.fen2yuanStr(item.getPrice()));
                helper.setTextColor(R.id.tv_qiugou, activity.getResources().getColor(R.color.color_727BD5));

            } else {
                helper.setText(R.id.orderNumber, "出售数量：" + item.getOrderNumber());
                helper.setText(R.id.saleedNumber, "已售数量：" + item.getSaleedNumber());
                helper.setText(R.id.saleNumber, "待售数量：" + item.getSaleNumber());
                helper.setTextColor(R.id.saleNumber, activity.getResources().getColor(R.color.color_F76646));
                helper.setTextColor(R.id.tv_unitprice, activity.getResources().getColor(R.color.color_F76646));
                helper.setTextColor(R.id.tv_qiugou_unitprice, activity.getResources().getColor(R.color.color_F76646));
                helper.setText(R.id.tv_qiugou, MathUtils.fen2yuanStr(item.getPrice()));
                helper.setTextColor(R.id.tv_qiugou, activity.getResources().getColor(R.color.color_F76646));
            }

            helper.addOnClickListener(R.id.button);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
