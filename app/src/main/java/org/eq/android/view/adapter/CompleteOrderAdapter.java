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
import org.eq.android.entity.CompleteOrder;
import org.eq.android.entity.OrderTradeProcessing;
import org.eq.android.entity.Product;

import java.util.List;

public class CompleteOrderAdapter extends BaseQuickAdapter<CompleteOrder, BaseViewHolder> {

    Activity activity;

    public CompleteOrderAdapter(@Nullable List<CompleteOrder> data, Activity activity) {
        super(R.layout.item_complete_order, data);
        this.activity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, CompleteOrder item) {
        try {
            helper.setText(R.id.tv_name, "装风的匣子");

            helper.setText(R.id.productNameTextView, item.getProductName());
            helper.setText(R.id.productPriceTextView, ""+item.getUnitPrice());
//            helper.setText(R.id.tv_sum, "共计"+item.getOrderNum()+"张");
//            helper.setText(R.id.tv_hejiprice, "￥"+item.getAmount());

//            helper.addOnClickListener(R.id.button);

            Glide.with(activity).load(item.getUserHeadImg()).into((ImageView) helper.getView(R.id.iv_head));
//        Glide.with(activity).load(item.getImg()).into((ImageView) helper.getView(R.id.iv_vip));
            Glide.with(activity).load(item.getProductImg()).into((ImageView) helper.getView(R.id.productPictureImageView));

            String timef=item.getFinishTime();
            String st=timef.split(" ")[0];
            String status = item.getStatus();
//            int type = item.getType();
            String orderWantNumberRemark=item.getOrderWantNumberRemark();
            String orderFinishNumberRemark=item.getOrderFinishNumberRemark();
            if (!TextUtils.isEmpty(orderWantNumberRemark)){
                helper.setText(R.id.tv_sum, orderWantNumberRemark);
            }

            if (!TextUtils.isEmpty(orderFinishNumberRemark)){
                helper.setText(R.id.tv_hejiprice, orderFinishNumberRemark);
            }

            helper.setText(R.id.tv_ongoing_pay, status +"时间: "+ st);
           /* if (type==1){
                helper.setText(R.id.tv_sum, "发布出售：  "+item.getOrderAdNum());
                helper.setText(R.id.tv_hejiprice, "已售：  "+item.getOrderAdTradeNum());
            }else if(type==2){
                helper.setText(R.id.tv_sum, "发布求购：  "+item.getOrderAdNum());
                helper.setText(R.id.tv_hejiprice, "已购：  "+item.getOrderAdTradeNum());
            }else if(type==3){
                helper.setText(R.id.tv_sum, "已售：  "+item.getOrderTradeNum());
                helper.setVisible(R.id.tv_hejiprice, false);

            }else if(type==4){
                helper.setText(R.id.tv_sum, "求购：  "+item.getOrderTradeNum());
                helper.setVisible(R.id.tv_hejiprice, false);

            }*/


            helper.setText(R.id.tv_pay_status, "已"+item.getStatus());

        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }
}
