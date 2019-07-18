package org.eq.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.githang.statusbar.StatusBarCompat;
import com.orhanobut.logger.Logger;

import org.eq.android.R;
import org.eq.android.api.ADOrderApi;
import org.eq.android.api.NetFactory;
import org.eq.android.api.ProductApi;
import org.eq.android.api.ResponseData;
import org.eq.android.common.Constant;
import org.eq.android.entity.ADOrder;
import org.eq.android.entity.OverDue;
import org.eq.android.entity.Product;
import org.eq.android.utils.SystemPreferences;
import org.eq.android.utils.DisplayUtil;
import org.eq.android.utils.ToastUtil;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liufan on 2019/6/12.
 * 数字劵详情
 */
public class DigitalDescriptionDetail extends BaseActivity {

    @BindView(R.id.productPictureImageView)
    ImageView productPictureImageView;
    @BindView(R.id.productNameTextView)
    TextView productNameTextView;
    @BindView(R.id.priceTextView)
    TextView priceTextView;
    @BindView(R.id.productLogImageView)
    ImageView productLogImageView;
    @BindView(R.id.productStoreNameTextView)
    TextView productStoreNameTextView;
    @BindView(R.id.dateTextView)
    TextView dateTextView;


    @BindView(R.id.available_num)
    TextView available_num;
    @BindView(R.id.lock_num)
    TextView lock_num;
    @BindView(R.id.sum_num)
    TextView sum_num;

    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.tv_content)
    TextView tv_content;

    @BindView(R.id.ivcorn)
    ImageView ivcorn;
    @BindView(R.id.ivContent)
    TextView ivContent;

    @BindView(R.id.chengduishuoming_content)
    TextView chengduishuoming_content;

    @BindView(R.id.falvzhichi_content)
    TextView falvzhichi_content;

    @BindView(R.id.iv_back)
    ImageView iv_back;


    Product product;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_digital_description_detail;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.colorwhite), true);
        DisplayUtil.expandTouchArea(iv_back);
        int productId = getIntent().getIntExtra("productId", 0);
        initData(productId);
    }

    private void initData(int productId) {

        NetFactory.create(ProductApi.class).getMyDetails((int)getUserId(), productId).enqueue(new Callback<ResponseData<Product>>() {
            @Override
            public void onResponse(Call<ResponseData<Product>> call, Response<ResponseData<Product>> response) {
                ResponseData<Product> responseData = response.body();
                if (responseData != null && responseData.getStatus() == 200 && responseData.getData() != null) {
                    product = responseData.getData();
                    try {
                        initView();
                    } catch (Exception e) {
                        Logger.e(e.toString());
                    }
                } else {
                    ToastUtil.showLongToast(responseData.getErrMsg());
                }
            }

            @Override
            public void onFailure(Call<ResponseData<Product>> call, Throwable t) {
                ToastUtil.showLongToast("网络错误");
            }
        });
    }

    private void initView() {
        Glide.with(this)
                .load(product.getImg())
                .into(productPictureImageView);
        productNameTextView.setText(product.getProductName());
        priceTextView.setText(new BigDecimal(product.getUnitPrice()).divide(new BigDecimal(100)).toString());
        Glide.with(this)
                .load(product.getIssuerImg())
                .into(productLogImageView);
        productStoreNameTextView.setText(product.getAcceptName());

        String expirationDate = "";
        if (product.getExpirationStart() == null || product.getExpirationEnd() == null) {
            expirationDate = "永久有效";
        } else {
            expirationDate += product.getExpirationStart().split(" ")[0].replaceAll("-", ".") + " - ";
            expirationDate += product.getExpirationEnd().split(" ")[0].replaceAll("-", ".");
        }
        dateTextView.setText(expirationDate);

        available_num.setText(String.valueOf(product.getNumber() - product.getLockedNum()));
        lock_num.setText(String.valueOf(product.getLockedNum()));
        sum_num.setText(String.valueOf(product.getNumber()));

        tv_name.setText(product.getAcceptName());
        tv_content.setText(product.getAcceptIntro());

        Glide.with(this)
                .load(product.getIssuerImg())
                .into(ivcorn);
        ivContent.setText(product.getIssuerIntro());

        chengduishuoming_content.setText(product.getReceive());

        String text = "·本券在承兑过程中出现问题，由 " + "<font color='#F76646'>法律机构名称</font>" + " 提供法律咨询服务";
        falvzhichi_content.setText(Html.fromHtml(text));
    }

    @OnClick({R.id.tv_zhuanchu, R.id.tv_chengdui, R.id.tv_sell, R.id.iv_back})
    public void onclick(View view) {
        if (SystemPreferences.getInt(Constant.AUTHSTATUS)==2){
            switch (view.getId()) {

                case R.id.iv_back:
                    finish();
                    break;
                //转出
                case R.id.tv_zhuanchu:
                    break;
                //承兑
                case R.id.tv_chengdui:
                    Intent intent1 = new Intent(this, AcceptanceActivity.class);
                    OverDue overDue = new OverDue();
                    overDue.setProductId(String.valueOf(product.getId()));
                    overDue.setProductName(product.getProductName());
                    overDue.setImg(product.getImg());
                    overDue.setNumber(String.valueOf(product.getNumber()));
                    overDue.setUnitPrice(product.getUnitPrice());
                    intent1.putExtra("HOLDBEAN", overDue);
                    startActivity(intent1);
                    break;
                //售出
                case R.id.tv_sell:
                    Intent intent = new Intent(this, SellReleaseActivity.class);
                    intent.putExtra("productId", product.getId());
                    startActivity(intent);
                    break;
            }
        }else {
            startActivity(new Intent(this, UploadIDCardInformationActivity.class));
        }

    }
}
