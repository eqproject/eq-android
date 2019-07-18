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
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.footer.LoadingView;
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;

import org.eq.android.R;
import org.eq.android.api.EntityList;
import org.eq.android.api.NetFactory;
import org.eq.android.api.ProductApi;
import org.eq.android.api.ResponseData;
import org.eq.android.common.Constant;
import org.eq.android.utils.StatusBarHeightUtil;
import org.eq.android.entity.Product;
import org.eq.android.utils.ToastUtil;
import org.eq.android.view.adapter.ReleaseProductAdapter;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 求购卡券列表
 */
public class BuyProductListActivity extends BaseActivity {

    @BindView(R.id.refreshLayout)
    TwinklingRefreshLayout refreshLayout;
    @BindView(R.id.recycleView)
    RecyclerView recyclerView;

    @BindView(R.id.title_background)
    ImageView titleBackground;
    @BindView(R.id.barContainer)
    LinearLayout barContainer;


    LinkedList<Product> products = new LinkedList<>();
    ReleaseProductAdapter releaseProductAdapter;


    @Override
    public int getContentViewResId() {
        return R.layout.activity_buy_product_list;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        makeStatusBarTransprant();
        initStatusBarHeight();
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

        ConstraintLayout.LayoutParams layoutParams2 = (ConstraintLayout.LayoutParams) refreshLayout.getLayoutParams();
        layoutParams2.topMargin = StatusBarHeightUtil.subIOStatusBarHeight(this, layoutParams2.topMargin);
        refreshLayout.setLayoutParams(layoutParams2);

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
                refreshData(products, releaseProductAdapter, refreshLayout);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                loadMoreData(products, releaseProductAdapter, refreshLayout);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addItemDecoration(dividerItemDecoration);
        //设置 adapter
        releaseProductAdapter = new ReleaseProductAdapter(products, this);
        releaseProductAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                int productId = products.get(position).getId();
                Intent intent = new Intent(BuyProductListActivity.this, BuyReleaseActivity.class);
                intent.putExtra("productId", productId);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(releaseProductAdapter);
    }

    @OnClick({R.id.iv_back, R.id.help})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.help:
                //todo 跳转帮助页面
                break;
        }
    }

    /**
     * 刷新数据
     *
     * @param list
     * @param adapter
     * @param refreshLayout
     */
    private void refreshData(final List<Product> list, final BaseQuickAdapter adapter, final TwinklingRefreshLayout refreshLayout) {
        NetFactory.create(ProductApi.class).getPlatformEffect((int) getUserId(), Constant.pageSize, 1).enqueue(new Callback<ResponseData<EntityList<Product>>>() {
            @Override
            public void onResponse(Call<ResponseData<EntityList<Product>>> call, Response<ResponseData<EntityList<Product>>> response) {
                ResponseData<EntityList<Product>> responseData = response.body();
                if (responseData.getData() == null || responseData.getStatus() != 200 || responseData.getData().getList() == null || responseData.getData().getList().size() == 0) {
                    ToastUtil.showLongToast("没有更多数据");
                    refreshLayout.finishLoadmore();
                    return;
                }
                list.clear();
                list.addAll(responseData.getData().getList());
                adapter.notifyDataSetChanged();
                refreshLayout.finishRefreshing();
            }

            @Override
            public void onFailure(Call<ResponseData<EntityList<Product>>> call, Throwable t) {
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
    private void loadMoreData(final List<Product> list, final BaseQuickAdapter adapter, final TwinklingRefreshLayout refreshLayout) {

        int pageNum = list.size() / Constant.pageSize + 1;
        if (list.size() % Constant.pageSize != 0) {
            pageNum++;
        }

        NetFactory.create(ProductApi.class).getPlatformEffect((int) getUserId(), Constant.pageSize, pageNum).enqueue(new Callback<ResponseData<EntityList<Product>>>() {
            @Override
            public void onResponse(Call<ResponseData<EntityList<Product>>> call, Response<ResponseData<EntityList<Product>>> response) {
                ResponseData<EntityList<Product>> responseData = response.body();
                if (responseData.getData() == null || responseData.getStatus() != 200 || responseData.getData().getList() == null || responseData.getData().getList().size() == 0) {
                    ToastUtil.showLongToast("没有更多数据");
                    refreshLayout.finishLoadmore();
                    return;
                }
                list.addAll(responseData.getData().getList());
                adapter.notifyDataSetChanged();
                refreshLayout.finishLoadmore();
            }

            @Override
            public void onFailure(Call<ResponseData<EntityList<Product>>> call, Throwable t) {
                ToastUtil.showLongToast("网络错误");
            }
        });
    }
}
