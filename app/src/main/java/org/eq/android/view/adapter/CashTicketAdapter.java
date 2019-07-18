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

public class CashTicketAdapter extends BaseQuickAdapter<OverDue, BaseViewHolder> {

    Activity activity;

    public CashTicketAdapter(@Nullable List<OverDue> data, Activity activity) {
        super(R.layout.item_ticket_cash, data);
        this.activity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, OverDue item) {
        helper.setText(R.id.productNameTextView, item.getProductName());
        helper.setText(R.id.productNumberTextView, "￥" + MathUtils.fen2yuanStr(item.getUnitPrice()));
        helper.setText(R.id.lose_num, item.getNumber());
        helper.addOnClickListener(R.id.legal_support);
        helper.addOnClickListener(R.id.onsultants);

        Glide.with(activity)
                .load(item.getImg())
                .into((ImageView) helper.getView(R.id.productPictureImageView));
    }
}

