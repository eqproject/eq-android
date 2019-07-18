package org.eq.android.activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.eq.android.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by liufan on 2019/7/14.
 */
//发布中详情
public class ReleaseOrderDetailActivity extends BaseActivity{
    @BindView(R.id.release_tv_title)
    TextView release_tv_title;
    @BindView(R.id.release_tv_time)
    TextView release_tv_time;
    @BindView(R.id.relativelayout)
    TextView relativelayout;
    @BindView(R.id.productPictureImageView)
    AppCompatImageView productPictureImageView;
    @BindView(R.id.productNameTextView)
    AppCompatTextView productNameTextView;
    @BindView(R.id.priceTextView)
    AppCompatTextView priceTextView;
    @BindView(R.id.dateTextView)
    AppCompatTextView dateTextView;
    @BindView(R.id.orderNumber)
    TextView orderNumber;
    @BindView(R.id.saleedNumber)
    TextView saleedNumber;
    @BindView(R.id.saleNumber)
    TextView saleNumber;
    @BindView(R.id.tv_qiugou)
    TextView tv_qiugou;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_releaseorder_detail;
    }

    @Override
    public void init(Bundle savedInstanceState) {

        initDate();

    }

    private void initDate(){
        release_tv_title.setText("已发布求购广告");
        release_tv_time.setText("发布时间："+"2019-03-23");
        relativelayout.setText("求购600面值Nike Sportswear Heritage 券");
        productNameTextView.setText("Nike Sportswear Heritage" +"券");
        priceTextView.setText("600");
        dateTextView.setText("2019.03.23-2049.12.31");
        orderNumber.setText("求购数量：10");
        saleNumber.setText("待购数量：10");
        tv_qiugou.setText("550");

        //商品头像
        Glide.with(this).load(getResources().getDrawable(R.mipmap.eq_head)).into(productPictureImageView);

    }

    @OnClick({R.id.title_back})
    public void onclick(View view){
        switch (view.getId()){
            case R.id.title_back:
                finish();
                break;
        }
    }
}
