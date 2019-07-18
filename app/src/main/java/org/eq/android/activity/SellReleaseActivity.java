package org.eq.android.activity;

import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import com.bumptech.glide.Glide;
import com.githang.statusbar.StatusBarCompat;

import org.eq.android.R;
import org.eq.android.api.ADOrderApi;
import org.eq.android.api.NetFactory;
import org.eq.android.api.ProductApi;
import org.eq.android.api.ResponseData;
import org.eq.android.utils.DisplayUtil;
import org.eq.android.entity.ADOrder;
import org.eq.android.entity.Product;
import org.eq.android.utils.MathUtils;
import org.eq.android.utils.ToastUtil;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 发布出售
 */
public class SellReleaseActivity extends BaseActivity {


    @BindView(R.id.productPictureImageView)
    AppCompatImageView productPirctureImageView;
    @BindView(R.id.productNameTextView)
    AppCompatTextView productNameTextView;
    @BindView(R.id.priceTextView)
    AppCompatTextView priceTextView;
    @BindView(R.id.orderTitleEditText)
    AppCompatEditText orderTitleEditText;
    @BindView(R.id.orderPriceEditText)
    AppCompatEditText orderPriceEditText;
    @BindView(R.id.unitTextView)
    AppCompatTextView unitTextView;
    @BindView(R.id.orderNumEditText)
    AppCompatEditText orderNumEditText;
    @BindView(R.id.rmbTextView)
    AppCompatTextView rmbTextView;
    @BindView(R.id.totalPriceTextView)
    AppCompatTextView totalPriceTextView;
    @BindView(R.id.warnTextView)
    AppCompatTextView warnTextView;

    @BindView(R.id.loadingDialog)
    ConstraintLayout loadingDialog;
    @BindView(R.id.loadingImageView)
    AppCompatImageView loadingImageView;

    @BindView(R.id.successDialog)
    ConstraintLayout successDialog;


    Product product;

    int price; //单价 分
    int num; //数量 个

    int maxNum;


    @Override
    public int getContentViewResId() {
        return R.layout.activity_sell_release;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.colorwhite), true);
        initView();

        int productId = getIntent().getIntExtra("productId", 0);
        initData(productId);
    }

    /**
     * 初始化 view
     */
    private void initView() {

        //设置底部提示文字
        SpannableString spannableString = new SpannableString("  注意：\n发布出售信息记录求购者的待支付金额，当有用户出售相应数量卡券给求购者时，此时求购者需在半小时内登录APP，确认订单后并支付相应的金额，即可获得相应数量的卡券！");
        //获取Drawable资源
        Drawable drawable = getResources().getDrawable(R.mipmap.ic_warn);
        drawable.setBounds(0, 0, DisplayUtil.sp2px(this, 14), DisplayUtil.sp2px(this, 14));
        //创建ImageSpan
        ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
        //用ImageSpan替换文本
        spannableString.setSpan(span, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        warnTextView.setText(spannableString);

        //设置loading 动画
        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_circle_rotate);
        LinearInterpolator interpolator = new LinearInterpolator();
        rotateAnimation.setInterpolator(interpolator);
        loadingImageView.startAnimation(rotateAnimation);

        //设置金额输入框
        orderPriceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (TextUtils.isEmpty(s)) {
                    return;
                }

                if (s.toString().equals("¥")) {
                    orderPriceEditText.setText("");
                    return;
                }

                if (!s.toString().startsWith("¥")) {
                    String str = "¥" + s.toString();
                    orderPriceEditText.setText(str);
                    orderPriceEditText.setSelection(str.length());
                    return;
                }

                //删除.后面超过两位的数字
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        orderPriceEditText.setText(s);
                        orderPriceEditText.setSelection(s.length());
                        return;
                    }
                }

                //如果.在起始位置,则起始位置自动补0
                if (s.toString().trim().substring(1).startsWith(".")) {
                    orderPriceEditText.setText("¥0");
                    orderPriceEditText.setSelection(2);
                    return;
                }


                //如果起始位置为0并且第二位跟的不是".",则无法后续输入
                if (s.toString().substring(1).startsWith("0")
                        && s.toString().length() > 2
                        && !s.toString().substring(2).startsWith(".")) {
                    orderPriceEditText.setText("¥0");
                    orderPriceEditText.setSelection(2);
                    return;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();

                if (SellReleaseActivity.this.price != 0 && s.length() == 1 && !"¥".equals(str)) {
                    return;
                }

                if (!TextUtils.isEmpty(str) && str.startsWith("¥") && !"¥".equals(str) && !"¥.".equals(str) && !"¥0.".equals(str)) {
                    BigDecimal price = new BigDecimal(str.substring(1));
                    price = price.multiply(new BigDecimal(100));
                    SellReleaseActivity.this.price = price.intValue();
                } else {
                    SellReleaseActivity.this.price = 0;
                }

                calculateTotalPrice();
            }
        });

        //设置数量输入框
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
                    if (i > maxNum) {
                        orderNumEditText.setText(String.valueOf(maxNum));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                if (!TextUtils.isEmpty(str)) {
                    SellReleaseActivity.this.num = Math.min(Integer.parseInt(str), maxNum);
                } else {
                    SellReleaseActivity.this.num = 0;
                }

                calculateTotalPrice();

            }
        });
    }

    //计算总价
    private void calculateTotalPrice() {
        if (num != 0 && price != 0) {
            BigDecimal totalPrice = new BigDecimal(num).multiply(new BigDecimal(price)).divide(new BigDecimal(100));
            totalPriceTextView.setText(totalPrice.toString());
            totalPriceTextView.setVisibility(View.VISIBLE);
            rmbTextView.setVisibility(View.VISIBLE);
        } else {
            totalPriceTextView.setText(String.valueOf(0.0));
            totalPriceTextView.setVisibility(View.VISIBLE);
            rmbTextView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 初始化数据
     */
    private void initData(int productId) {
        NetFactory.create(ProductApi.class).getMyDetails((int) getUserId(), productId).enqueue(new Callback<ResponseData<Product>>() {
            @Override
            public void onResponse(Call<ResponseData<Product>> call, Response<ResponseData<Product>> response) {
                ResponseData<Product> responseData = response.body();
                if (responseData != null && responseData.getStatus() == 200) {
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

    /**
     * 设置
     */
    private void setData() {
        Glide.with(this)
                .load(product.getImg())
                .into(productPirctureImageView);
        productNameTextView.setText(product.getProductName());
        priceTextView.setText(MathUtils.fen2yuanStr(product.getUnitPrice()));
        orderTitleEditText.setText("出售" + MathUtils.fen2yuanStr(product.getUnitPrice()) + "面值" + product.getProductName());

        maxNum = product.getNumber() - product.getLockedNum();
        orderNumEditText.setHint("最多可出售" + maxNum + "张");
    }


    @OnClick({R.id.iv_back, R.id.sellAllTextView, R.id.releaseSellButton})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.sellAllTextView:
                orderNumEditText.setText(String.valueOf(maxNum));
                break;
            case R.id.releaseSellButton:

                String adTitle = orderTitleEditText.getText().toString();
                int userId = (int) getUserId();
                int orderType = 1;

                if (TextUtils.isEmpty(adTitle) || price == 0 || num == 0) {
                    ToastUtil.showLongToast("请设置价格和数量");
                    return;
                }

                showLoadingDialog();

                NetFactory.create(ADOrderApi.class).createADOrder(product.getId(), adTitle, price, num, userId, orderType).enqueue(new Callback<ResponseData<ADOrder>>() {
                    @Override
                    public void onResponse(Call<ResponseData<ADOrder>> call, Response<ResponseData<ADOrder>> response) {
                        ResponseData<ADOrder> responseData = response.body();
                        if (responseData != null && responseData.getStatus() == 200) {
                            ADOrder adOrder = responseData.getData();
                            successDialog.setVisibility(View.VISIBLE);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 1000);
                        } else {
                            ToastUtil.showLongToast("设置失败");
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseData<ADOrder>> call, Throwable t) {
                        ToastUtil.showLongToast("网络错误");
                    }
                });

                break;
        }
    }

    private void showLoadingDialog() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(loadingDialog, "alpha", 0f, 1f);
        animator.setDuration(400);
        animator.start();
        loadingDialog.setVisibility(View.VISIBLE);
    }

}
