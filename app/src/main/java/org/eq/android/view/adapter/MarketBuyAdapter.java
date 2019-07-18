package org.eq.android.view.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.eq.android.R;
import org.eq.android.entity.ADOrder;
import org.eq.android.utils.MathUtils;

import java.util.List;

public class MarketBuyAdapter extends BaseQuickAdapter<ADOrder, BaseViewHolder> {

    Fragment fragment;

    public MarketBuyAdapter(@Nullable List<ADOrder> data, Fragment fragment) {
        super(R.layout.item_market_buy, data);
        this.fragment = fragment;
    }

    @Override
    protected void convert(BaseViewHolder helper, ADOrder item) {

        try {
            helper.setText(R.id.productNameTextView, item.getTitle());
            helper.setText(R.id.productNumberTextView, String.valueOf(item.getSaleNumber()));
            helper.setText(R.id.priceTextView, MathUtils.fen2yuanStr(item.getPrice()));
            if (item.getUserBoundState() == 1) {
                helper.getView(R.id.aliPayImageView).setVisibility(View.VISIBLE);
                helper.getView(R.id.wechatPayImageView).setVisibility(View.GONE);
            } else if (item.getUserBoundState() == 2) {
                helper.getView(R.id.aliPayImageView).setVisibility(View.GONE);
                helper.getView(R.id.wechatPayImageView).setVisibility(View.VISIBLE);
            } else {
                helper.getView(R.id.aliPayImageView).setVisibility(View.VISIBLE);
                helper.getView(R.id.wechatPayImageView).setVisibility(View.VISIBLE);
            }

            helper.addOnClickListener(R.id.buyButton);

            Glide.with(fragment)
                    .load(item.getImg())
                    .into((ImageView) helper.getView(R.id.productPictureImageView));
            Glide.with(fragment)
                    .load(item.getUserImg())
                    .into((ImageView) helper.getView(R.id.avatarImageView));

            helper.setText(R.id.nameTextView, item.getNickName().substring(0, 1) + "**");
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
