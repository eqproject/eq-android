package org.eq.android.fargment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.footer.LoadingView;
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;
import com.zhengsr.viewpagerlib.indicator.TabIndicator;

import org.eq.android.R;
import org.eq.android.activity.ADOrderDetailActivity;
import org.eq.android.activity.AcceptanceActivity;
import org.eq.android.activity.BaseActivity;
import org.eq.android.activity.DigitalDescriptionDetail;
import org.eq.android.activity.ImportTicketActivity;
import org.eq.android.activity.InAcceptanceInfoActivity;
import org.eq.android.activity.ReleaseActivity;
import org.eq.android.activity.SellReleaseActivity;
import org.eq.android.activity.UploadIDCardInformationActivity;
import org.eq.android.api.ADOrderApi;
import org.eq.android.api.EntityList;
import org.eq.android.api.NetFactory;
import org.eq.android.api.ResponseData;
import org.eq.android.api.VoucherApi;
import org.eq.android.common.Constant;
import org.eq.android.entity.ADOrder;
import org.eq.android.entity.AcceptDetail;
import org.eq.android.entity.OverDue;
import org.eq.android.entity.ResponseNode;
import org.eq.android.utils.DisplayUtil;
import org.eq.android.utils.SystemPreferences;
import org.eq.android.utils.ToastUtil;
import org.eq.android.view.adapter.CashTicketAdapter;
import org.eq.android.view.adapter.HoldTicketAdatper;
import org.eq.android.view.adapter.LoseTicketAdapter;
import org.eq.android.view.adapter.MarketBuyAdapter;
import org.eq.android.view.adapter.MarketSellAdapter;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 券包
 */
public class TicketFragment extends Fragment {

    @BindView(R.id.iv_import)
    ImageView iv_import;
    @BindView(R.id.iv_question)
    ImageView iv_question;

    @BindView(R.id.line_indicator)
    TabIndicator tabIndicator;


    @BindView(R.id.viewPager)
    ViewPager viewPager;

    LinkedList<TwinklingRefreshLayout> viewList = new LinkedList<>();

    //持有劵
    LinkedList<OverDue> holdTicks = new LinkedList<>();
    HoldTicketAdatper holdTicketAdatper;
    RecyclerView holdRecyclerView;

    //承兑劵
    LinkedList<OverDue> cashTicks = new LinkedList<>();
    CashTicketAdapter cashTicketAdapter;
    RecyclerView cashRecyclerView;

    //无效劵
    LinkedList<OverDue> loseTicks = new LinkedList<>();
    LoseTicketAdapter loseTicketAdapter;
    RecyclerView loseRecyclerView;

    private Unbinder unbinder;

    private TicketBroadcast ticketBroadcast;

    public TicketFragment() {
    }

    public static TicketFragment newInstance() {
        TicketFragment fragment = new TicketFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ticket, container, false);
        //注册 binder
        unbinder = ButterKnife.bind(this, view);

        DisplayUtil.expandTouchArea(iv_question);
        DisplayUtil.expandTouchArea(iv_import);

        initViewPager();
        initRecyclerView();
        initBroadcast();

        viewList.get(0).startRefresh();
        viewList.get(1).startRefresh();
        viewList.get(2).startRefresh();

        return view;
    }

    private void initBroadcast(){
        ticketBroadcast = new TicketBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.BROADCAST_ACTION_TICKET);
        getActivity().registerReceiver(ticketBroadcast, intentFilter);
    }


    /**
     * 初始化 viewpager
     */
    private void initViewPager() {

        LinkedList<String> titleList = new LinkedList<>();
        titleList.addLast("持有劵");
        titleList.addLast("承兑中");
        titleList.addLast("无效劵");

        viewList.addLast(getRefreshLayout(R.layout.common_recycler_view));
        viewList.addLast(getRefreshLayout(R.layout.common_recycler_view));
        viewList.addLast(getRefreshLayout(R.layout.common_recycler_view));


        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
                return view == o;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                viewPager.addView(viewList.get(position));
                return viewList.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                viewPager.removeView(viewList.get(position));
            }
        });

        tabIndicator.setViewPagerSwitchSpeed(viewPager, 600);
        tabIndicator.setTabData(viewPager, titleList, new TabIndicator.TabClickListener() {
            @Override
            public void onClick(int position) {
                //顶部点击的方法公布出来

                viewPager.setCurrentItem(position);
            }
        });

        iv_import.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ImportTicketActivity.class));
            }
        });
    }

    private TwinklingRefreshLayout getRefreshLayout(int id) {
        TwinklingRefreshLayout refreshLayout = (TwinklingRefreshLayout) getLayoutInflater().inflate(id, null);

        SinaRefreshView sinaRefreshView = new SinaRefreshView(getActivity());
        sinaRefreshView.setTextColor(0xff8B8989);

        refreshLayout.setHeaderView(sinaRefreshView);

        LoadingView loadingView = new LoadingView(getActivity());
        refreshLayout.setBottomView(loadingView);
        return refreshLayout;
    }

    /**
     * 初始化三个recyclerview
     */
    private void initRecyclerView() {

        //item分割线
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.shape_market_recycle_view_divider);
        dividerItemDecoration.setDrawable(drawable);


        //初始化承兑中
        TwinklingRefreshLayout sellRefreshView = viewList.get(1);
        //监听 下拉刷新 上拉加载
        sellRefreshView.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                refreshCashData(cashTicks, cashTicketAdapter, refreshLayout);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                loadMoreCashData(cashTicks, cashTicketAdapter, refreshLayout);
            }
        });

        cashRecyclerView = sellRefreshView.findViewById(R.id.recycleView);
        cashRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        cashRecyclerView.setItemAnimator(new DefaultItemAnimator());

        cashRecyclerView.addItemDecoration(dividerItemDecoration);
        //设置 adapter
        cashTicketAdapter = new CashTicketAdapter(cashTicks, getActivity());
        cashTicketAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getActivity(), InAcceptanceInfoActivity.class);
                intent.putExtra("id", cashTicks.get(position).getProductId());
                startActivity(intent);
            }
        });
        cashTicketAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    //法务支持
                    case R.id.legal_support:
                        showNet();

                        break;
                    //咨询商家
                    case R.id.onsultants:
                        showBusinessContact();
                        break;
                }

            }
        });
        cashRecyclerView.setAdapter(cashTicketAdapter);


        //初始化持有劵
        TwinklingRefreshLayout buyRefreshView = viewList.get(0);
        //监听 下拉刷新 上拉加载
        buyRefreshView.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                refreshData(holdTicks, holdTicketAdatper, refreshLayout);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                loadMoreData(holdTicks, holdTicketAdatper, refreshLayout);
            }
        });

        holdRecyclerView = buyRefreshView.findViewById(R.id.recycleView);
        holdRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        holdRecyclerView.setItemAnimator(new DefaultItemAnimator());


        holdRecyclerView.addItemDecoration(dividerItemDecoration);
        //设置 adapter
        holdTicketAdatper = new HoldTicketAdatper(holdTicks, getActivity());
        holdTicketAdatper.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String productId = holdTicks.get(position).getProductId();
                Intent intent = new Intent(getActivity(), DigitalDescriptionDetail.class);
                intent.putExtra("productId", Integer.parseInt(productId));
                startActivity(intent);
            }
        });
        holdTicketAdatper.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                //承兑
                if (view.getId() == R.id.legal_support) {
                    if (SystemPreferences.getInt(Constant.AUTHSTATUS) == 2) {
                        Intent intent = new Intent(getActivity(), AcceptanceActivity.class);
                        intent.putExtra("HOLDBEAN", holdTicks.get(position));
                        startActivity(intent);
                    } else {
                        startActivity(new Intent(getActivity(), UploadIDCardInformationActivity.class));
                    }

                }

                //非商户没有出售，只有转出
                if (SystemPreferences.getInt(Constant.CLIENTTYPE)!=2){
                    //转出
                    if (view.getId() == R.id.onsultants) {
                       /* if (SystemPreferences.getInt(Constant.AUTHSTATUS) == 2) {
                            Intent intent = new Intent(getActivity(), SellReleaseActivity.class);
                            intent.putExtra("productId", Integer.parseInt(holdTicks.get(position).getProductId()));
                            startActivity(intent);
                        } else {
                            startActivity(new Intent(getActivity(), UploadIDCardInformationActivity.class));
                        }*/
                    }
                }else{
                //商户：出售，转出
                    //转出
                    if (view.getId() == R.id.turn_out){
                        ToastUtil.showShortToast("转出");
                    }

                    //出售
                    if (view.getId() == R.id.onsultants) {
                        if (SystemPreferences.getInt(Constant.AUTHSTATUS) == 2) {
                            Intent intent = new Intent(getActivity(), SellReleaseActivity.class);
                            intent.putExtra("productId", Integer.parseInt(holdTicks.get(position).getProductId()));
                            startActivity(intent);
                        } else {
                            startActivity(new Intent(getActivity(), UploadIDCardInformationActivity.class));
                        }
                    }
                }

            }
        });
        holdRecyclerView.setAdapter(holdTicketAdatper);

        //初始化无效卷
        TwinklingRefreshLayout buyRefreshView2 = viewList.get(2);
        //监听 下拉刷新 上拉加载
        buyRefreshView2.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                refreshLoseData(loseTicks, loseTicketAdapter, refreshLayout);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                loadMoreLoseData(loseTicks, loseTicketAdapter, refreshLayout);
            }
        });

        loseRecyclerView = buyRefreshView2.findViewById(R.id.recycleView);
        loseRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        loseRecyclerView.setItemAnimator(new DefaultItemAnimator());

        loseRecyclerView.addItemDecoration(dividerItemDecoration);
        //设置 adapter
        loseTicketAdapter = new LoseTicketAdapter(loseTicks, getActivity());
        loseRecyclerView.setAdapter(loseTicketAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //注销 binder
        unbinder.unbind();

        if (ticketBroadcast != null){
            getActivity().unregisterReceiver(ticketBroadcast);
        }
    }

    private void showNet() {
        NetFactory.create(VoucherApi.class).getSupportLegal().enqueue(new Callback<ResponseData<ResponseNode>>() {
            @Override
            public void onResponse(Call<ResponseData<ResponseNode>> call, Response<ResponseData<ResponseNode>> response) {
                ResponseData<ResponseNode> responseData = response.body();
                if (responseData.getStatus() == 200) {
                    ResponseNode responseNode = responseData.getData();
                    showDialog("法务支持");
                } else {
                    Log.e("", "onResponse: " + responseData.getStatus() + "..." + responseData.getErrMsg());
                }
            }

            @Override
            public void onFailure(Call<ResponseData<ResponseNode>> call, Throwable t) {

            }
        });
    }

    private void showDialog(String content) {
        //自定义的弹窗布局
        View contentViewi = View.inflate(getActivity(), R.layout.dialog_fawu, null);
        RelativeLayout button = (RelativeLayout) contentViewi.findViewById(R.id.button);


        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(contentViewi);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);
        alertDialog.show();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void showBusinessContact() {
        //自定义的弹窗布局
        View contentViewi = View.inflate(getActivity(), R.layout.dialog_business_contact, null);
        TextView tv_ok = (TextView) contentViewi.findViewById(R.id.tv_ok);
        final TextView call_number = (TextView) contentViewi.findViewById(R.id.call_number);
        call_number.setText("010-12345678");


        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(contentViewi);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        call_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

                callPhone(call_number.getText().toString());
            }
        });
    }

    /**
     * 拨打电话（跳转到拨号界面，用户手动点击拨打）
     *
     * @param phoneNum 电话号码
     */
    public void callPhone(String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        startActivity(intent);
    }

    @OnClick({R.id.iv_question, R.id.iv_import})
    public void onclick(View view) {
        switch (view.getId()) {
            //问题
            case R.id.iv_question:

                break;
            //导入卡劵
            case R.id.iv_import:
                startActivity(new Intent(getActivity(), ImportTicketActivity.class));
                break;
        }
    }

    /**
     * 持有劵 刷新数据
     */
    private void refreshData(final List<OverDue> list, final BaseQuickAdapter adapter, final TwinklingRefreshLayout refreshLayout) {
        NetFactory.create(VoucherApi.class).getEffectList((int) ((BaseActivity) getActivity()).getUserId(), Constant.pageSize, 1).enqueue(new Callback<ResponseData<EntityList<OverDue>>>() {
            @Override
            public void onResponse(Call<ResponseData<EntityList<OverDue>>> call, Response<ResponseData<EntityList<OverDue>>> response) {
                ResponseData<EntityList<OverDue>> responseData = response.body();
                if (responseData.getStatus() == 200) {
                    EntityList<OverDue> overDueList = responseData.getData();
                    if (overDueList != null) {
                        list.clear();
                        list.addAll(responseData.getData().getList());
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    ToastUtil.showLongToast(responseData.getErrMsg());
                }
                refreshLayout.finishRefreshing();
            }

            @Override
            public void onFailure(Call<ResponseData<EntityList<OverDue>>> call, Throwable t) {
                ToastUtil.showLongToast("网络错误");
            }
        });
    }

    /**
     * 持有劵 加载更多
     */
    private void loadMoreData(final List<OverDue> list, final BaseQuickAdapter adapter, final TwinklingRefreshLayout refreshLayout) {
        int pageNum = list.size() / Constant.pageSize + 1;
        if (list.size() % Constant.pageSize != 0) {
            pageNum++;
        }

        NetFactory.create(VoucherApi.class).getEffectList((int) ((BaseActivity) getActivity()).getUserId(), Constant.pageSize, pageNum).enqueue(new Callback<ResponseData<EntityList<OverDue>>>() {
            @Override
            public void onResponse(Call<ResponseData<EntityList<OverDue>>> call, Response<ResponseData<EntityList<OverDue>>> response) {
                ResponseData<EntityList<OverDue>> responseData = response.body();
                if (responseData.getStatus() == 200) {
                    EntityList<OverDue> overDueList = responseData.getData();
                    if (overDueList != null && overDueList.getList().size() > 0) {
                        list.clear();
                        list.addAll(responseData.getData().getList());
                        adapter.notifyDataSetChanged();
                    } else {
                        ToastUtil.showLongToast("没有更多数据");
                    }
                } else {
                    ToastUtil.showLongToast(responseData.getErrMsg());
                }
                refreshLayout.finishLoadmore();
            }

            @Override
            public void onFailure(Call<ResponseData<EntityList<OverDue>>> call, Throwable t) {
                ToastUtil.showLongToast("网络错误");
            }
        });
    }

    /**
     * 刷新承兑中数据
     */
    private void refreshCashData(final List<OverDue> list, final BaseQuickAdapter adapter, final TwinklingRefreshLayout refreshLayout) {
        NetFactory.create(VoucherApi.class).getAcceptList((int) ((BaseActivity) getActivity()).getUserId(), Constant.pageSize, 1).enqueue(new Callback<ResponseData<EntityList<OverDue>>>() {
            @Override
            public void onResponse(Call<ResponseData<EntityList<OverDue>>> call, Response<ResponseData<EntityList<OverDue>>> response) {
                ResponseData<EntityList<OverDue>> responseData = response.body();
                if (responseData.getStatus() == 200) {
                    EntityList<OverDue> overDueList = responseData.getData();
                    if (overDueList != null) {
                        list.clear();
                        list.addAll(responseData.getData().getList());
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    ToastUtil.showLongToast(responseData.getErrMsg());
                }

                refreshLayout.finishRefreshing();
            }

            @Override
            public void onFailure(Call<ResponseData<EntityList<OverDue>>> call, Throwable t) {
                ToastUtil.showLongToast("网络错误");
            }
        });
    }

    /**
     * 加载承兑中更多
     */
    private void loadMoreCashData(final List<OverDue> list, final BaseQuickAdapter adapter, final TwinklingRefreshLayout refreshLayout) {
        int pageNum = list.size() / Constant.pageSize + 1;
        if (list.size() % Constant.pageSize != 0) {
            pageNum++;
        }

        NetFactory.create(VoucherApi.class).getAcceptList((int) ((BaseActivity) getActivity()).getUserId(), Constant.pageSize, pageNum).enqueue(new Callback<ResponseData<EntityList<OverDue>>>() {
            @Override
            public void onResponse(Call<ResponseData<EntityList<OverDue>>> call, Response<ResponseData<EntityList<OverDue>>> response) {
                ResponseData<EntityList<OverDue>> responseData = response.body();
                if (responseData.getStatus() == 200) {
                    EntityList<OverDue> overDueList = responseData.getData();
                    if (overDueList != null && overDueList.getList().size() > 0) {
                        list.clear();
                        list.addAll(responseData.getData().getList());
                        adapter.notifyDataSetChanged();
                    } else {
                        ToastUtil.showLongToast("没有更多数据");
                    }
                } else {
                    ToastUtil.showLongToast(responseData.getErrMsg());
                }
                refreshLayout.finishLoadmore();
            }

            @Override
            public void onFailure(Call<ResponseData<EntityList<OverDue>>> call, Throwable t) {
                ToastUtil.showLongToast("网络错误");
            }
        });
    }

    /**
     * 刷新无效数据
     */
    private void refreshLoseData(final List<OverDue> list, final BaseQuickAdapter adapter, final TwinklingRefreshLayout refreshLayout) {
        NetFactory.create(VoucherApi.class).getOverDueList((int) ((BaseActivity) getActivity()).getUserId(), Constant.pageSize, 1).enqueue(new Callback<ResponseData<EntityList<OverDue>>>() {
            @Override
            public void onResponse(Call<ResponseData<EntityList<OverDue>>> call, Response<ResponseData<EntityList<OverDue>>> response) {
                ResponseData<EntityList<OverDue>> responseData = response.body();
                if (responseData.getStatus() == 200) {
                    EntityList<OverDue> overDueList = responseData.getData();
                    if (overDueList != null) {
                        list.clear();
                        list.addAll(responseData.getData().getList());
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    ToastUtil.showLongToast(responseData.getErrMsg());
                }

                refreshLayout.finishRefreshing();
            }

            @Override
            public void onFailure(Call<ResponseData<EntityList<OverDue>>> call, Throwable t) {
                ToastUtil.showLongToast("网络错误");
            }
        });
    }

    /**
     * 加载无效更多
     */
    private void loadMoreLoseData(final List<OverDue> list, final BaseQuickAdapter adapter, final TwinklingRefreshLayout refreshLayout) {
        int pageNum = list.size() / Constant.pageSize + 1;
        if (list.size() % Constant.pageSize != 0) {
            pageNum++;
        }

        NetFactory.create(VoucherApi.class).getOverDueList((int) ((BaseActivity) getActivity()).getUserId(), Constant.pageSize, pageNum).enqueue(new Callback<ResponseData<EntityList<OverDue>>>() {
            @Override
            public void onResponse(Call<ResponseData<EntityList<OverDue>>> call, Response<ResponseData<EntityList<OverDue>>> response) {
                ResponseData<EntityList<OverDue>> responseData = response.body();
                if (responseData.getStatus() == 200) {
                    EntityList<OverDue> overDueList = responseData.getData();
                    if (overDueList != null && overDueList.getList().size() > 0) {
                        list.clear();
                        list.addAll(responseData.getData().getList());
                        adapter.notifyDataSetChanged();
                    } else {
                        ToastUtil.showLongToast("没有更多数据");
                    }
                } else {
                    ToastUtil.showLongToast(responseData.getErrMsg());
                }
                refreshLayout.finishLoadmore();
            }

            @Override
            public void onFailure(Call<ResponseData<EntityList<OverDue>>> call, Throwable t) {
                ToastUtil.showLongToast("网络错误");
            }
        });
    }

    private class TicketBroadcast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int type=intent.getIntExtra("type",0);
            if (type==1){
                viewList.get(0).startRefresh();
            }else if(type==2){
                viewList.get(1).startRefresh();
            }else if(type==3){
                viewList.get(2).startRefresh();
            }
        }
    }

}
