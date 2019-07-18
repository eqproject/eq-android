package org.eq.android.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.squareup.picasso.Picasso;

import org.eq.android.R;
import org.eq.android.api.NetFactory;
import org.eq.android.api.ResponseData;
import org.eq.android.api.VoucherApi;
import org.eq.android.entity.AcceptDetail;
import org.eq.android.entity.OverDue;
import org.eq.android.utils.DisplayUtil;
import org.eq.android.utils.StatusBarHeightUtil;
import org.eq.android.utils.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liufan on 2019/6/13.
 */
//承兑
public class AcceptanceActivity extends BaseActivity {
    @BindView(R.id.title_background)
    ImageView titleBackground;
    @BindView(R.id.barContainer)
    LinearLayout barContainer;
    @BindView(R.id.iv_back)
    ImageView iv_back;


    @BindView(R.id.orderNumEditText)
    AppCompatEditText orderNumEditText;
    @BindView(R.id.productNameTextView)
    AppCompatTextView productNameTextView;
    @BindView(R.id.priceTextView)
    AppCompatTextView priceTextView;
    @BindView(R.id.productPictureImageView)
    AppCompatImageView productPictureImageView;
    @BindView(R.id.acceptance_sum_num)
    TextView acceptance_sum_num;
    @BindView(R.id.sellAllTextView)
    AppCompatTextView sellAllTextView;
    @BindView(R.id.et_contacts)
    EditText et_contacts;
    @BindView(R.id.et_phone)
    EditText et_phone;
    @BindView(R.id.et_address)
    EditText et_address;
    @BindView(R.id.button)
    RelativeLayout button;

    private OverDue mOverDue;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_acceptance;
    }

    @Override
    public void init(Bundle savedInstanceState) {

        makeStatusBarTransprant();
        initStatusBarHeight();
        DisplayUtil.expandTouchArea(iv_back);

        mOverDue = (OverDue) getIntent().getSerializableExtra("HOLDBEAN");

        if (mOverDue != null) {
            initData(mOverDue);
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

    //计算 statusbar 高度
    private void initStatusBarHeight() {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) titleBackground.getLayoutParams();
        layoutParams.height = StatusBarHeightUtil.subIOStatusBarHeight(this, layoutParams.height);
        titleBackground.setLayoutParams(layoutParams);

        ConstraintLayout.LayoutParams layoutParams1 = (ConstraintLayout.LayoutParams) barContainer.getLayoutParams();
        layoutParams1.topMargin = StatusBarHeightUtil.subIOStatusBarHeight(this, layoutParams1.topMargin);
        barContainer.setLayoutParams(layoutParams1);
    }

    private void initData(OverDue overDue) {

        Picasso.get().load(overDue.getImg()).into(productPictureImageView);
        productNameTextView.setText(overDue.getProductName());
        priceTextView.setText(overDue.getNumber());
        orderNumEditText.setHint("最多可使用承兑卡券数量" + overDue.getNumber() + "张");
        orderNumEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(TextUtils.isEmpty(s)){
                    acceptance_sum_num.setText("0");
                }else{
                    acceptance_sum_num.setText(s.toString());
                }
            }
        });
    }

    @OnClick({R.id.iv_back, R.id.sellAllTextView, R.id.button})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            //全部
            case R.id.sellAllTextView:
                orderNumEditText.setText(mOverDue.getNumber());
                acceptance_sum_num.setText(mOverDue.getNumber());
                break;
            //发起承兑申请
            case R.id.button:

                int number = Integer.parseInt(acceptance_sum_num.getText().toString().trim());
                String consignee = et_contacts.getText().toString().trim();
                String consigneePhone = et_phone.getText().toString().trim();
                String consigneeAddress = et_address.getText().toString().trim();

                if (number > 0 && !TextUtils.isEmpty(consignee) && !TextUtils.isEmpty(consigneePhone) && !TextUtils.isEmpty(consigneeAddress)) {
                    NetFactory.create(VoucherApi.class).getAccept((int) getUserId(), mOverDue.getProductId(), number, consignee, consigneePhone, consigneeAddress).enqueue(new Callback<ResponseData<AcceptDetail>>() {
                        @Override
                        public void onResponse(Call<ResponseData<AcceptDetail>> call, Response<ResponseData<AcceptDetail>> response) {

                            ResponseData<AcceptDetail> acceptDetailResponseData = response.body();
                            if (acceptDetailResponseData.getStatus() == 200) {
                                String id = acceptDetailResponseData.getData().getAcceptCode();
                                Intent intent = new Intent(AcceptanceActivity.this, InAcceptanceInfoActivity.class);
                                intent.putExtra("id", id);
                                startActivity(intent);
                                finish();
                            } else {
                                ToastUtil.showLongToast(acceptDetailResponseData.getErrMsg());
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseData<AcceptDetail>> call, Throwable t) {
                            ToastUtil.showLongToast("网络错误");
                        }
                    });
                } else {
                    Toast.makeText(this, "数量，联系人，手机号，收货地址都不能为空", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }
}
