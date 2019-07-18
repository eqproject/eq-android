package org.eq.android.view.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import org.eq.android.R;
import org.eq.android.entity.ADOrder;
import org.eq.android.entity.OverDue;
import org.eq.android.utils.MathUtils;

import java.util.List;

/**
 * Created by liufan on 2019/6/9.
 */

public class LoseTicketAdapter extends BaseQuickAdapter<OverDue, BaseViewHolder> {

    Activity activity;

    public LoseTicketAdapter(@Nullable List<OverDue> data, Activity activity) {
        super(R.layout.item_ticket_lose, data);
        this.activity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, OverDue item) {
        try {
            helper.setText(R.id.productNameTextView, item.getProductName());
            helper.setText(R.id.productNumberTextView, "￥" + MathUtils.fen2yuanStr(item.getUnitPrice()));
            helper.setText(R.id.lose_num, item.getNumber());
            helper.addOnClickListener(R.id.buyButton);

            Glide.with(activity)
                    .load(item.getImg())
                    .into((ImageView) helper.getView(R.id.productPictureImageView));

            if ("已出售".equals(item.getOverdueReason())) {
                Glide.with(activity)
                        .load(R.mipmap.eq_saled)
                        .into((ImageView) helper.getView(R.id.statusImageView));
            } else {
                Glide.with(activity)
                        .load(R.mipmap.eq_expired)
                        .into((ImageView) helper.getView(R.id.statusImageView));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
