package org.eq.android.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.githang.statusbar.StatusBarCompat;
import com.youth.banner.Banner;

import org.eq.android.R;
import org.eq.android.api.NetFactory;
import org.eq.android.api.OrderApi;
import org.eq.android.api.ProductApi;
import org.eq.android.api.ResponseData;
import org.eq.android.entity.OrderTrade;
import org.eq.android.entity.Product;
import org.eq.android.utils.MathUtils;
import org.eq.android.utils.ToastUtil;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 出售设置
 */
public class SellSettingActivity extends BaseActivity {


    @BindView(R.id.top_content)
    View top_content;
    @BindView(R.id.priceTitleTextView)
    TextView priceTitleTextView;
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
    @BindView(R.id.orderNumEditText)
    TextView orderNumEditText;
    @BindView(R.id.buyNumTextView)
    TextView buyNumTextView;
    @BindView(R.id.totalPriceTextView)
    TextView totalPriceTextView;


    String orderCode;
    int price;
    int productId;
    int orderNumber;
    int saleedNumber;
    int saleNumber;
    int maxNumber;
    int num;


    Product product;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_sell_setting;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        makeStatusBarTransprant();

        ObjectAnimator animator = ObjectAnimator.ofFloat(top_content, "alpha", 0f, 0f, 0.5f);
        animator.setDuration(800);
        animator.start();

        Intent intent = getIntent();
        orderCode = intent.getStringExtra("orderCode");
        price = intent.getIntExtra("price", 0);
        productId = intent.getIntExtra("productId", 0);
        orderNumber = intent.getIntExtra("orderNumber", 0);
        saleedNumber = intent.getIntExtra("saleedNumber", 0);
        saleNumber = intent.getIntExtra("saleNumber", 0);

        priceTitleTextView.setText("¥" + MathUtils.fen2yuanStr(price));

        orderNumEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if (str.equals("0")) {
                    orderNumEditText.setText("");
                    return;
                }

                if (!TextUtils.isEmpty(str)) {
                    int i = Integer.parseInt(str);
                    if (i > maxNumber) {
                        orderNumEditText.setText(String.valueOf(maxNumber));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                if (!TextUtils.isEmpty(str)) {
                    SellSettingActivity.this.num = Math.min(Integer.parseInt(str), maxNumber);
                } else {
                    SellSettingActivity.this.num = 0;
                }

                calculateTotalPrice();

            }
        });

        initData();
    }

    //计算总价
    private void calculateTotalPrice() {
        if (num != 0 && price != 0) {
            BigDecimal totalPrice = new BigDecimal(num).multiply(new BigDecimal(price)).divide(new BigDecimal(100));
            totalPriceTextView.setText(totalPrice.toString());
        } else {
            totalPriceTextView.setText(String.valueOf(0.0));
        }
    }

    private void makeStatusBarTransprant() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void initData() {
        NetFactory.create(ProductApi.class).getMyDetails((int) getUserId(), productId).enqueue(new Callback<ResponseData<Product>>() {
            @Override
            public void onResponse(Call<ResponseData<Product>> call, Response<ResponseData<Product>> response) {
                ResponseData<Product> responseData = response.body();
                if (responseData != null && responseData.getStatus() == 200 && responseData.getData() != null) {
                    product = responseData.getData();
                    setData();
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

    private void setData() {
        maxNumber = Math.min(product.getNumber() - product.getLockedNum(), saleNumber);
        Glide.with(this)
                .load(product.getImg())
                .into(productPictureImageView);
        productNameTextView.setText(product.getProductName());
        priceTextView.setText(MathUtils.fen2yuanStr(product.getUnitPrice()));
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

        orderNumEditText.setHint("最多可出售" + maxNumber + "张");
        buyNumTextView.setText(String.valueOf(maxNumber));
    }


    @OnClick({R.id.closeImageView, R.id.sellButton, R.id.sellAllTextView, R.id.top_content})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.top_content:
                finish();
                break;
            case R.id.closeImageView:
                finish();
                break;
            case R.id.sellAllTextView:
                orderNumEditText.setText(String.valueOf(maxNumber));
                break;
            case R.id.sellButton:

                if (num == 0) {
                    ToastUtil.showLongToast("请先设置数量");
                }

                NetFactory.create(OrderApi.class).createOrder((int) getUserId(), orderCode, num, price, " ").enqueue(new Callback<ResponseData<OrderTrade>>() {
                    @Override
                    public void onResponse(Call<ResponseData<OrderTrade>> call, Response<ResponseData<OrderTrade>> response) {
                        ResponseData<OrderTrade> responseData = response.body();
                        if (responseData != null && responseData.getStatus() == 200 && responseData.getData() != null) {
                            Intent intentBroadcast=new Intent();
                            intentBroadcast.putExtra("type",2);
                            sendBroadcast(intentBroadcast);

                            Intent intent = new Intent(SellSettingActivity.this, SellFinishActivity.class);
                            intent.putExtra("tradeNo", responseData.getData().getTradeNo());
                            startActivity(intent);
                            finish();
                        } else {
                            ToastUtil.showLongToast(responseData.getErrMsg());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseData<OrderTrade>> call, Throwable t) {
                        ToastUtil.showLongToast("网络错误");
                    }
                });

                break;
        }
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.activity_bottom_exit);
        ObjectAnimator animator = ObjectAnimator.ofFloat(top_content, "alpha", 0.5f, 0f);
        animator.setDuration(100);
        animator.start();
    }
}
