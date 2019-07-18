package org.eq.android.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.footer.LoadingView;
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;

import org.eq.android.R;
import org.eq.android.api.ADOrderApi;
import org.eq.android.api.EntityList;
import org.eq.android.api.NetFactory;
import org.eq.android.api.ResponseData;
import org.eq.android.api.VoucherApi;
import org.eq.android.common.Constant;
import org.eq.android.entity.OrderTradeProcessingNew;
import org.eq.android.utils.DisplayUtil;
import org.eq.android.utils.StatusBarHeightUtil;
import org.eq.android.utils.ToastUtil;
import org.eq.android.view.adapter.ReleasingAdapter;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liufan on 2019/6/14.
 */
//发布中
public class ReleasingListActivity extends BaseActivity {

    @BindView(R.id.title_background)
    ImageView titleBackground;
    @BindView(R.id.barContainer)
    LinearLayout barContainer;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.help)
    ImageView help;

    @BindView(R.id.refreshLayout)
    TwinklingRefreshLayout refreshLayout;
    @BindView(R.id.recycleView)
    RecyclerView recyclerView;
    @BindView(R.id.ongoing_constrainlayout)
    ConstraintLayout ongoing_constrainlayout;

    LinkedList<OrderTradeProcessingNew> products = new LinkedList<>();
    ReleasingAdapter releasingAdapter;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_releasing;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        makeStatusBarTransprant();
        initStatusBarHeight();
        DisplayUtil.expandTouchArea(iv_back);
        DisplayUtil.expandTouchArea(help);

        initRecycleView();

        refreshLayout.startRefresh();
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


    private void initRecycleView() {

        //设置 refreshLayout 的刷新动画
        SinaRefreshView sinaRefreshView = new SinaRefreshView(this);
        sinaRefreshView.setTextColor(0xff8B8989);

        refreshLayout.setHeaderView(sinaRefreshView);

        LoadingView loadingView = new LoadingView(this);
        refreshLayout.setBottomView(loadingView);

        //item分割线
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.shape_market_recycle_view_divider);
        dividerItemDecoration.setDrawable(drawable);

        //监听 下拉刷新 上拉加载
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                refreshData(products, releasingAdapter, refreshLayout);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                loadMoreData(products, releasingAdapter, refreshLayout);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addItemDecoration(dividerItemDecoration);
        //设置 adapter
        releasingAdapter = new ReleasingAdapter(products, this);
        releasingAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.button) {
                    showCloseDialog(products.get(position).getOrderCode(), position);
                }
            }
        });
        recyclerView.setAdapter(releasingAdapter);
    }

    /**
     * 刷新数据
     *
     * @param list
     * @param adapter
     * @param refreshLayout
     */
    private void refreshData(final List<OrderTradeProcessingNew> list, final BaseQuickAdapter adapter, final TwinklingRefreshLayout refreshLayout) {
        NetFactory.create(VoucherApi.class).getUserAdorderList((int) getUserId(), Constant.pageSize, 1,1).enqueue(new Callback<ResponseData<EntityList<OrderTradeProcessingNew>>>() {
            @Override
            public void onResponse(Call<ResponseData<EntityList<OrderTradeProcessingNew>>> call, Response<ResponseData<EntityList<OrderTradeProcessingNew>>> response) {
                ResponseData<EntityList<OrderTradeProcessingNew>> responseData = response.body();

                if (responseData.getStatus() == 200) {
                    if (responseData.getData().getList().size() > 0) {
                        ongoing_constrainlayout.setVisibility(View.GONE);
                        list.clear();
                        list.addAll(responseData.getData().getList());
                        adapter.notifyDataSetChanged();
                    } else {
                        ongoing_constrainlayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    ongoing_constrainlayout.setVisibility(View.VISIBLE);
                    ToastUtil.showLongToast(responseData.getErrMsg());
                }

                refreshLayout.finishRefreshing();
            }

            @Override
            public void onFailure(Call<ResponseData<EntityList<OrderTradeProcessingNew>>> call, Throwable t) {
                ToastUtil.showLongToast("网络错误");
            }
        });

    }

    /**
     * 加载更多
     *
     * @param list
     * @param adapter
     * @param refreshLayout
     */
    private void loadMoreData(final List<OrderTradeProcessingNew> list, final BaseQuickAdapter adapter, final TwinklingRefreshLayout refreshLayout) {

        int pageNum = list.size() / Constant.pageSize + 1;
        if (list.size() % Constant.pageSize != 0) {
            pageNum++;
        }

        NetFactory.create(VoucherApi.class).getUserAdorderList((int) getUserId(), Constant.pageSize, pageNum,1).enqueue(new Callback<ResponseData<EntityList<OrderTradeProcessingNew>>>() {
            @Override
            public void onResponse(Call<ResponseData<EntityList<OrderTradeProcessingNew>>> call, Response<ResponseData<EntityList<OrderTradeProcessingNew>>> response) {
                ResponseData<EntityList<OrderTradeProcessingNew>> responseData = response.body();
                if (responseData.getStatus() == 200) {
                    if (responseData.getData().getList().size() > 0) {
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
            public void onFailure(Call<ResponseData<EntityList<OrderTradeProcessingNew>>> call, Throwable t) {
                ToastUtil.showLongToast("网络错误");
            }
        });
    }

    @OnClick({R.id.iv_back})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    /**
     * 展示 关闭 对话框
     */
    private void showCloseDialog(final String orderCode, final int position) {
        final MaterialDialog dialog = new MaterialDialog.Builder(ReleasingListActivity.this)
                .customView(R.layout.dialog_cancel_order, false)
                .show();
        View customView = dialog.getCustomView();
        View tv_ok = customView.findViewById(R.id.tv_ok);
        View call_number = customView.findViewById(R.id.call_number);

        call_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetFactory.create(ADOrderApi.class).cancelADOrder(orderCode, (int) getUserId()).enqueue(new Callback<ResponseData>() {
                    @Override
                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                        ResponseData responseData = response.body();
                        if (responseData.getStatus() == 200) {
                            products.remove(position);
                            releasingAdapter.notifyDataSetChanged();
                            ToastUtil.showLongToast("订单已关闭");
                        } else {
                            ToastUtil.showLongToast(responseData.getErrMsg());
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<ResponseData> call, Throwable t) {
                        ToastUtil.showLongToast("网络错误");
                        dialog.dismiss();
                    }
                });
            }
        });
    }

}
