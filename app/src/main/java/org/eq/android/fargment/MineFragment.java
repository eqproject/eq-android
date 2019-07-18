package org.eq.android.fargment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;

import org.eq.android.R;
import org.eq.android.activity.CompleteOrderListActivity;
import org.eq.android.activity.OngoingListActivity;
import org.eq.android.activity.PersonalDataActivity;
import org.eq.android.activity.RealNameuthenticationActivity;
import org.eq.android.activity.ReleasingListActivity;
import org.eq.android.activity.SetActivity;
import org.eq.android.activity.UploadIDCardInformationActivity;
import org.eq.android.activity.WaitPayListActivity;
import org.eq.android.api.NetFactory;
import org.eq.android.api.OrderApi;
import org.eq.android.api.ResponseData;
import org.eq.android.api.UserApi;
import org.eq.android.common.Constant;
import org.eq.android.entity.PoolInfo;
import org.eq.android.entity.User;
import org.eq.android.utils.MyUtil;
import org.eq.android.utils.SystemPreferences;
import org.eq.android.utils.ToastUtil;
import org.eq.android.view.NumImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 我的
 */
public class MineFragment extends Fragment {

    @BindView(R.id.relativelayout_auth)
    RelativeLayout relativelayout_auth;
    @BindView(R.id.relativelayout_money)
    RelativeLayout relativelayout_money;
    @BindView(R.id.relativelayout_auth2)
    RelativeLayout relativelayout_auth2;
    @BindView(R.id.relativelayout_auth3)
    RelativeLayout relativelayout_auth3;
    @BindView(R.id.relativelayout_auth4)
    RelativeLayout relativelayout_auth4;
    @BindView(R.id.relativelayout_auth5)
    RelativeLayout relativelayout_auth5;
    @BindView(R.id.relativelayout_auth6)
    RelativeLayout relativelayout_auth6;
    @BindView(R.id.relativelayout_auth7)
    RelativeLayout relativelayout_auth7;
    @BindView(R.id.relativelayout_auth8)
    RelativeLayout relativelayout_auth8;
    @BindView(R.id.realativelayout1)
    RelativeLayout realativelayout1;
    @BindView(R.id.iv_pay)
    NumImageView iv_pay;
    @BindView(R.id.iv_ing)
    NumImageView iv_ing;
    @BindView(R.id.iv_realse)
    NumImageView iv_realse;
    @BindView(R.id.iv_compelte)
    NumImageView iv_compelte;
    @BindView(R.id.mine_iv)
    ImageView mine_iv;
    @BindView(R.id.mine_name)
    TextView mine_name;
    @BindView(R.id.mine_phone)
    TextView mine_phone;
    @BindView(R.id.relativelayout_release)
    RelativeLayout relativelayout_release;

    //参数名
    private static final String ARG_PARAM1 = "param1";

    private String mParam1;

    private Unbinder unbinder;

    //待支付，进行中消息数
    private int sMsWaitPayNum,sMsPoolInfo;



    public MineFragment() {
    }

    /**
     * Use this factory method to create a new instance o
     */
    public static MineFragment newInstance(String param1) {
        MineFragment fragment = new MineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        //注册 binder
        unbinder = ButterKnife.bind(this, view);

        iv_pay.setNum(sMsWaitPayNum);
        iv_ing.setNum(sMsPoolInfo);

        return view;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //注销 binder
        unbinder.unbind();
    }

    @OnClick({R.id.relativelayout_auth,R.id.relativelayout_money,R.id.relativelayout_auth2,R.id.relativelayout_auth3,
            R.id.relativelayout_auth4,R.id.relativelayout_auth5,R.id.relativelayout_auth6,R.id.relativelayout_auth7,
            R.id.relativelayout_auth8,R.id.relayout_ongoing,R.id.iv_pay_rl,R.id.realse_ry,R.id.compelte_rl,R.id.realativelayout1,
            R.id.relativelayout_release
    })
    public void onclick(View view){
        //检验是否登录
        if (MyUtil.isLoginDeal(getActivity())){

        switch (view.getId()){
            //待付款
            case R.id.iv_pay_rl:
                startActivity(new Intent(getActivity(), WaitPayListActivity.class));
                break;
            //进行中
            case R.id.relayout_ongoing:
                startActivity(new Intent(getActivity(), OngoingListActivity.class));
                break;
            //发布中
            case R.id.realse_ry:
                startActivity(new Intent(getActivity(), ReleasingListActivity.class));
                break;
            //已完成
            case R.id.compelte_rl:
                startActivity(new Intent(getActivity(), CompleteOrderListActivity.class));
                break;
            //实名认证
            case R.id.relativelayout_auth:
                if (SystemPreferences.getInt(Constant.AUTHSTATUS,0)==2){
                    startActivity(new Intent(getActivity(), RealNameuthenticationActivity.class));
                }else{
                    startActivity(new Intent(getActivity(), UploadIDCardInformationActivity.class));
                }

                break;
            case R.id.relativelayout_money:
                Toast.makeText(getActivity(),"资金账户地址管理",Toast.LENGTH_SHORT).show();

                break;
            case R.id.relativelayout_auth2:
                Toast.makeText(getActivity(),"推荐有礼",Toast.LENGTH_SHORT).show();

                break;
            case R.id.relativelayout_auth3:
                Toast.makeText(getActivity(),"联系客服",Toast.LENGTH_SHORT).show();

                break;
            case R.id.relativelayout_auth4:
                Toast.makeText(getActivity(),"意见反馈",Toast.LENGTH_SHORT).show();

                break;
            case R.id.relativelayout_auth5:
                Toast.makeText(getActivity(),"常见问题",Toast.LENGTH_SHORT).show();

                break;
            case R.id.relativelayout_auth6:
                Toast.makeText(getActivity(),"平台协议",Toast.LENGTH_SHORT).show();

                break;
            case R.id.relativelayout_auth7:
                Toast.makeText(getActivity(),"关于我们",Toast.LENGTH_SHORT).show();
                break;
            //设置
            case R.id.relativelayout_auth8:
                startActivity(new Intent(getActivity(), SetActivity.class));
                break;
            //个人资料
            case R.id.realativelayout1:
                startActivity(new Intent(getActivity(), PersonalDataActivity.class));
                break;
            //发布中的广告
            case R.id.relativelayout_release:
                startActivity(new Intent(getActivity(), ReleasingListActivity.class));
                break;
        }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //是否登录，登录了获取数量
        if (MyUtil.isLogin()){

            //商家的时候显示
            if (SystemPreferences.getInt(Constant.CLIENTTYPE)!=2){
                relativelayout_release.setVisibility(View.GONE);
            }else{
                relativelayout_release.setVisibility(View.VISIBLE);
            }

            NetFactory.create(OrderApi.class).poolInfo(SystemPreferences.getLong(Constant.USER_ID)).enqueue(new Callback<ResponseData<PoolInfo>>() {
                @Override
                public void onResponse(Call<ResponseData<PoolInfo>> call, Response<ResponseData<PoolInfo>> response) {
                    ResponseData<PoolInfo> responseData =response.body();
                    if (responseData.getStatus()==200){
                        PoolInfo poolInfo= responseData.getData();
                        sMsWaitPayNum =poolInfo.getWaitPay();
                        sMsPoolInfo = poolInfo.getProgress();

                        iv_pay.setNum(sMsWaitPayNum);
                        iv_ing.setNum(sMsPoolInfo);
                    }else{
                        Log.e("...", "onResponse: "+responseData.getErrMsg()+"..."+responseData.getStatus() );
                    }
                }

                @Override
                public void onFailure(Call<ResponseData<PoolInfo>> call, Throwable t) {

                }
            });


            //获取用户资料
            NetFactory.create(UserApi.class).getInfo(SystemPreferences.getLong(Constant.USER_ID)).enqueue(new Callback<ResponseData<User>>() {
                @Override
                public void onResponse(Call<ResponseData<User>> call, Response<ResponseData<User>> response) {
                    ResponseData<User> responseData = response.body();
                    if (responseData.getStatus() == 200) {
                        User overDue = responseData.getData();
                        mine_name.setText(overDue.getNickname());
                        mine_phone.setText(overDue.getName());
                        mine_phone.setText(overDue.getName());
                        if(!TextUtils.isEmpty(overDue.getPhotoHead())){
                            Glide.with(MineFragment.this).load(overDue.getPhotoHead()).into(mine_iv);
                        }
                    }else{
                        ToastUtil.showLongToast(responseData.getErrMsg());
                    }
                }

                @Override
                public void onFailure(Call<ResponseData<User>> call, Throwable t) {
                    ToastUtil.showLongToast("网络错误");
                }
            });

        }else{
            Glide.with(MineFragment.this).load(getActivity().getResources().getDrawable(R.mipmap.eq_head)).into(mine_iv);
            mine_name.setText("昵称");
            mine_phone.setText("手机号");
        }
    }

}
