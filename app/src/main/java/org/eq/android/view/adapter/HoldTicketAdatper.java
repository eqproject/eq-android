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
import org.eq.android.common.Constant;
import org.eq.android.entity.ADOrder;
import org.eq.android.entity.OverDue;
import org.eq.android.utils.MathUtils;
import org.eq.android.utils.SystemPreferences;

import java.util.List;

/**
 * Created by liufan on 2019/6/9.
 */

public class HoldTicketAdatper extends BaseQuickAdapter<OverDue, BaseViewHolder> {

    Activity activity;

    public HoldTicketAdatper(@Nullable List<OverDue> data, Activity activity) {
        super(R.layout.item_ticket_hold, data);
        this.activity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, OverDue item) {
        try {
            helper.setText(R.id.productNameTextView, item.getProductName());
            helper.setText(R.id.productNumberTextView, "￥" + MathUtils.fen2yuanStr(item.getUnitPrice()));
            helper.setText(R.id.lose_num, item.getEffectNumber() + item.getLockNumber() + "");
            helper.setText(R.id.lose_num_tv, item.getEffectNumber() + "/" + item.getLockNumber());
            helper.addOnClickListener(R.id.legal_support);

            if (SystemPreferences.getInt(Constant.CLIENTTYPE)!=2){
                helper.setText(R.id.onsultants, "转出");
                helper.setVisible(R.id.turn_out,false);
            }else{
                helper.setVisible(R.id.turn_out,true);
            }


            helper.addOnClickListener(R.id.onsultants);
            helper.addOnClickListener(R.id.turn_out);

            Glide.with(activity)
                    .load(item.getImg())
                    .into((ImageView) helper.getView(R.id.productPictureImageView));

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}