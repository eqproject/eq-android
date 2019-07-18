package org.eq.android.activity;

import android.app.AlertDialog;
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
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.githang.statusbar.StatusBarCompat;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.footer.LoadingView;
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;

import org.eq.android.R;
import org.eq.android.api.EntityList;
import org.eq.android.api.NetFactory;
import org.eq.android.api.OrderApi;
import org.eq.android.api.ProductApi;
import org.eq.android.api.ResponseData;
import org.eq.android.api.VoucherApi;
import org.eq.android.common.Constant;
import org.eq.android.entity.OrderTrade;
import org.eq.android.entity.OrderTradeProcessing;
import org.eq.android.entity.OrderTradeWaitPaying;
import org.eq.android.entity.Product;
import org.eq.android.utils.DisplayUtil;
import org.eq.android.utils.StatusBarHeightUtil;
import org.eq.android.utils.SystemPreferences;
import org.eq.android.utils.ToastUtil;
import org.eq.android.view.adapter.CashTicketAdapter;
import org.eq.android.view.adapter.OngoingAdapter;
import org.eq.android.view.adapter.ReleaseProductAdapter;
import org.eq.android.view.adapter.WaitPayingAdapter;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liufan on 2019/6/14.
 * //待付款
 */
public class WaitPayListActivity extends BaseActivity {

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

    LinkedList<OrderTradeWaitPaying> products = new LinkedList<>();
    WaitPayingAdapter waitPayingAdapter;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_wait_pay;
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
                refreshData(products, waitPayingAdapter, refreshLayout);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                loadMoreData(products, waitPayingAdapter, refreshLayout);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        recyclerView.addItemDecoration(dividerItemDecoration);
        //设置 adapter
        waitPayingAdapter = new WaitPayingAdapter(products, this);
        waitPayingAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //订单详情
                String productId = products.get(position).getTrade().getTradeNo();
                Intent intent = new Intent(WaitPayListActivity.this, OrderDetailActivity.class);
                intent.putExtra("tradeNo", productId);
                startActivity(intent);
            }
        });
        waitPayingAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                //取消订单
                if (view.getId() == R.id.button_cancel) {
                    cancelPayDialog(products.get(position).getTrade().getTradeNo());
                }
                //支付
                if (view.getId() == R.id.button) {
                    Intent intent = new Intent(WaitPayListActivity.this, PayOrderActivity.class);
                    intent.putExtra("tradeNo", products.get(position).getTrade().getTradeNo());
                    startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(waitPayingAdapter);
    }

    /**
     * 刷新数据
     *
     * @param list
     * @param adapter
     * @param refreshLayout
     */
    private void refreshData(final List<OrderTradeWaitPaying> list, final BaseQuickAdapter adapter, final TwinklingRefreshLayout refreshLayout) {
        NetFactory.create(VoucherApi.class).getPayingList((int) getUserId(), Constant.pageSize, 1).enqueue(new Callback<ResponseData<EntityList<OrderTradeWaitPaying>>>() {
            @Override
            public void onResponse(Call<ResponseData<EntityList<OrderTradeWaitPaying>>> call, Response<ResponseData<EntityList<OrderTradeWaitPaying>>> response) {
                ResponseData<EntityList<OrderTradeWaitPaying>> responseData = response.body();

                if (responseData.getStatus() == 200) {
                    if (responseData.getData() != null && responseData.getData().getList() != null && responseData.getData().getList().size() > 0) {
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
            public void onFailure(Call<ResponseData<EntityList<OrderTradeWaitPaying>>> call, Throwable t) {
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
    private void loadMoreData(final List<OrderTradeWaitPaying> list, final BaseQuickAdapter adapter, final TwinklingRefreshLayout refreshLayout) {

        int pageNum = list.size() / Constant.pageSize + 1;
        if (list.size() % Constant.pageSize != 0) {
            pageNum++;
        }

        NetFactory.create(VoucherApi.class).getPayingList((int) getUserId(), Constant.pageSize, pageNum).enqueue(new Callback<ResponseData<EntityList<OrderTradeWaitPaying>>>() {
            @Override
            public void onResponse(Call<ResponseData<EntityList<OrderTradeWaitPaying>>> call, Response<ResponseData<EntityList<OrderTradeWaitPaying>>> response) {
                ResponseData<EntityList<OrderTradeWaitPaying>> responseData = response.body();
                if (responseData.getStatus() == 200) {
                    if (responseData.getData() != null && responseData.getData().getList() != null && responseData.getData().getList().size() > 0) {
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
            public void onFailure(Call<ResponseData<EntityList<OrderTradeWaitPaying>>> call, Throwable t) {
                ToastUtil.showLongToast("网络错误");
            }
        });
    }

    private View contentViewi;
    private TextView call_number;
    private TextView tv_ok;

    private void cancelPayDialog(final String tradeNo) {
        //自定义的弹窗布局
        contentViewi = View.inflate(this, R.layout.dialog_cancel_order, null);
        call_number = (TextView) contentViewi.findViewById(R.id.call_number);
        tv_ok = (TextView) contentViewi.findViewById(R.id.tv_ok);


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(contentViewi);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        call_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                //取消订单
                NetFactory.create(OrderApi.class).cancelTradeOrder(tradeNo, getUserId()).enqueue(new Callback<ResponseData<OrderTrade>>() {
                    @Override
                    public void onResponse(Call<ResponseData<OrderTrade>> call, Response<ResponseData<OrderTrade>> response) {
                        ResponseData<OrderTrade> responseData = response.body();
                        if (responseData.getStatus() == 200) {
                            refreshLayout.startRefresh();
                            ToastUtil.showLongToast("订单已取消");
                        } else {
                            ToastUtil.showLongToast(responseData.getErrMsg());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseData<OrderTrade>> call, Throwable t) {
                        ToastUtil.showLongToast("网络错误");
                    }
                });
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
