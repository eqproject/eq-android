package org.eq.android.view.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import org.eq.android.R;
import org.eq.android.entity.Product;
import org.eq.android.utils.MathUtils;

import java.util.List;

public class ReleaseProductAdapter extends BaseQuickAdapter<Product, BaseViewHolder> {

    Activity activity;

    public ReleaseProductAdapter(@Nullable List<Product> data, Activity activity) {
        super(R.layout.item_release_proudct, data);
        this.activity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, Product item) {
        helper.setText(R.id.productNameTextView, item.getProductName());
        helper.setText(R.id.priceTextView, MathUtils.fen2yuanStr(item.getUnitPrice()));

        Glide.with(activity)
                .load(item.getImg())
                .into((ImageView) helper.getView(R.id.productPictureImageView));

    }
}
