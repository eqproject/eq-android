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

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.githang.statusbar.StatusBarCompat;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.footer.LoadingView;
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;

import org.eq.android.R;
import org.eq.android.api.EntityList;
import org.eq.android.api.NetFactory;
import org.eq.android.api.ProductApi;
import org.eq.android.api.ResponseData;
import org.eq.android.api.VoucherApi;
import org.eq.android.common.Constant;
import org.eq.android.entity.CompleteOrder;
import org.eq.android.entity.OrderTradeProcessing;
import org.eq.android.entity.Product;
import org.eq.android.utils.DisplayUtil;
import org.eq.android.utils.StatusBarHeightUtil;
import org.eq.android.utils.ToastUtil;
import org.eq.android.view.adapter.CashTicketAdapter;
import org.eq.android.view.adapter.CompleteOrderAdapter;
import org.eq.android.view.adapter.OngoingAdapter;
import org.eq.android.view.adapter.ReleaseProductAdapter;

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
//完成
public class CompleteOrderListActivity extends BaseActivity {
    @BindView(R.id.title_background)
    ImageView titleBackground;
    @BindView(R.id.barContainer)
    LinearLayout barContainer;
    @BindView(R.id.iv_back)
    ImageView iv_back;

    @BindView(R.id.refreshLayout)
    TwinklingRefreshLayout refreshLayout;
    @BindView(R.id.recycleView)
    RecyclerView recyclerView;
    @BindView(R.id.ongoing_constrainlayout)
    ConstraintLayout ongoing_constrainlayout;

    LinkedList<CompleteOrder> products = new LinkedList<>();
    CompleteOrderAdapter completeOrderAdapter;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_completeorder;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        makeStatusBarTransprant();
        initStatusBarHeight();
        DisplayUtil.expandTouchArea(iv_back);

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
                refreshData(products, completeOrderAdapter, refreshLayout);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                loadMoreData(products, completeOrderAdapter, refreshLayout);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addItemDecoration(dividerItemDecoration);
        //设置 adapter
        completeOrderAdapter = new CompleteOrderAdapter(products, this);
        completeOrderAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                try {
                    String productId = products.get(position).getOrderNo();
                    Intent intent = new Intent(CompleteOrderListActivity.this, OrderDetailActivity.class);
                    if (products.get(position).getType()==1 || products.get(position).getType()==2){
                        productId = products.get(position).getOrderNo();
                    }else if(products.get(position).getType()==3 || products.get(position).getType()==4){
                        productId = products.get(position).getTradeNo();
                    }
                    intent.putExtra("tradeNo", productId);
                    intent.putExtra("type", products.get(position).getType());
                    intent.putExtra("CompleteOrder", "CompleteOrder");
                    intent.putExtra("typetitle", products.get(position).getStatus());
                    startActivity(intent);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }
        });
        recyclerView.setAdapter(completeOrderAdapter);
    }

    /**
     * 刷新数据
     *
     * @param list
     * @param adapter
     * @param refreshLayout
     */
    private void refreshData(final List<CompleteOrder> list, final BaseQuickAdapter adapter, final TwinklingRefreshLayout refreshLayout) {
        NetFactory.create(VoucherApi.class).getFinishList((int) getUserId(), Constant.pageSize, 1).enqueue(new Callback<ResponseData<EntityList<CompleteOrder>>>() {
            @Override
            public void onResponse(Call<ResponseData<EntityList<CompleteOrder>>> call, Response<ResponseData<EntityList<CompleteOrder>>> response) {
                ResponseData<EntityList<CompleteOrder>> responseData = response.body();

                if (responseData.getStatus() == 200) {
                    EntityList<CompleteOrder> list1 = responseData.getData();
                    if (list1 != null && list1.getList().size() > 0) {
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
            public void onFailure(Call<ResponseData<EntityList<CompleteOrder>>> call, Throwable t) {
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
    private void loadMoreData(final List<CompleteOrder> list, final BaseQuickAdapter adapter, final TwinklingRefreshLayout refreshLayout) {
        int pageNum = list.size() / Constant.pageSize + 1;
        if (list.size() % Constant.pageSize != 0) {
            pageNum++;
        }

        NetFactory.create(VoucherApi.class).getFinishList((int) getUserId(), Constant.pageSize, pageNum).enqueue(new Callback<ResponseData<EntityList<CompleteOrder>>>() {
            @Override
            public void onResponse(Call<ResponseData<EntityList<CompleteOrder>>> call, Response<ResponseData<EntityList<CompleteOrder>>> response) {
                ResponseData<EntityList<CompleteOrder>> responseData = response.body();
                if (responseData.getStatus() == 200) {
                    EntityList<CompleteOrder> list1 = responseData.getData();
                    if (list1 != null && list1.getList().size() > 0) {
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
            public void onFailure(Call<ResponseData<EntityList<CompleteOrder>>> call, Throwable t) {
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

}
