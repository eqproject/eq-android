package org.eq.android.fargment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.footer.LoadingView;
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;
import com.orhanobut.logger.Logger;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.loader.ImageLoader;
import com.zhengsr.viewpagerlib.indicator.TabIndicator;

import org.eq.android.R;
import org.eq.android.activity.ADOrderDetailActivity;
import org.eq.android.activity.BuySettingActivity;
import org.eq.android.activity.LoginActivity;
import org.eq.android.activity.MainActivity;
import org.eq.android.activity.ReleaseActivity;
import org.eq.android.activity.SellSettingActivity;
import org.eq.android.activity.UploadIDCardInformationActivity;
import org.eq.android.api.ADOrderApi;
import org.eq.android.api.EntityList;
import org.eq.android.api.NetFactory;
import org.eq.android.api.ResponseData;
import org.eq.android.common.Constant;
import org.eq.android.utils.MyUtil;
import org.eq.android.utils.StatusBarHeightUtil;
import org.eq.android.entity.ADOrder;
import org.eq.android.utils.SystemPreferences;
import org.eq.android.utils.ToastUtil;
import org.eq.android.view.adapter.MarketBuyAdapter;
import org.eq.android.view.adapter.MarketSellAdapter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 集市
 */
public class MarketFragment extends Fragment {

    @BindView(R.id.banner)
    Banner banner;

    @BindView(R.id.line_indicator)
    TabIndicator tabIndicator;

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.tv_realse)
    TextView tv_realse;


    LinkedList<TwinklingRefreshLayout> viewList = new LinkedList<>();

    LinkedList<ADOrder> buyOrders = new LinkedList<>();
    RecyclerView buyRecyclerView;
    MarketBuyAdapter marketBuyAdapter;

    LinkedList<ADOrder> sellOrders = new LinkedList<>();
    RecyclerView sellRecyclerView;
    MarketSellAdapter marketSellAdapter;


    private Unbinder unbinder;

    boolean initSellRecycle; //是否初始化 我要卖

    private MarkerBroadcast markerBroadcast;


    public MarketFragment() {
    }

    /**
     * Use this factory method to create a new instance o
     */
    public static MarketFragment newInstance() {
        MarketFragment fragment = new MarketFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_market, container, false);
        //注册 binder
        unbinder = ButterKnife.bind(this, view);
        initStatusBarHeight();
        initBanner();
        initViewPager();
        initRecyclerView();

        //注册广播发布，出售成功都会收到广播
        initBroadcast();

        viewList.get(0).startRefresh();

        if (MyUtil.isLogin() && !initSellRecycle) {
            viewList.get(1).startRefresh();
            initSellRecycle = true;
        }

        return view;
    }

    //计算 statusbar 高度
    private void initStatusBarHeight() {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) banner.getLayoutParams();
        layoutParams.height = StatusBarHeightUtil.subIOStatusBarHeight(getActivity(), layoutParams.height);
        banner.setLayoutParams(layoutParams);

        ConstraintLayout.LayoutParams layoutParams1 = (ConstraintLayout.LayoutParams) tv_realse.getLayoutParams();
        layoutParams1.topMargin = StatusBarHeightUtil.subIOStatusBarHeight(getActivity(), layoutParams1.topMargin);
        tv_realse.setLayoutParams(layoutParams1);

        //商户显示发布
        if (MyUtil.isLoginDeal(getActivity())) {
            if (SystemPreferences.getInt(Constant.CLIENTTYPE)!=2){
                tv_realse.setVisibility(View.GONE);
            }else{
                tv_realse.setVisibility(View.VISIBLE);
            }
        }

    }

    private void initBroadcast(){
        markerBroadcast = new MarkerBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.BROADCAST_ACTION_MARK);
        getActivity().registerReceiver(markerBroadcast, intentFilter);
    }

    /**
     * 初始化 banner
     */
    private void initBanner() {

        tv_realse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MyUtil.isLoginDeal(getActivity())) {
                    if (SystemPreferences.getInt(Constant.AUTHSTATUS)==2){
                        startActivity(new Intent(getActivity(), ReleaseActivity.class));
                    }else{
                        startActivity(new Intent(getActivity(), UploadIDCardInformationActivity.class));
                    }
                }
            }
        });

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) banner.getLayoutParams();
        params.height = width * 334 / 750;
        banner.setLayoutParams(params);

        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        banner.setIndicatorGravity(BannerConfig.CENTER);
        banner.setBannerAnimation(Transformer.Default);
        banner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                imageView.setImageResource((Integer) path);
            }
        });
        ArrayList<Integer> strings = new ArrayList<>();
        strings.add(R.mipmap.banner_default_image);
        strings.add(R.mipmap.banner_default_image);
        strings.add(R.mipmap.banner_default_image);
        banner.setImages(strings);
        banner.start();

    }

    /**
     * 初始化 viewpager
     */
    private void initViewPager() {

        //添加两个标签
        LinkedList<String> titleList = new LinkedList<>();
        titleList.addLast("我要买");
        titleList.addLast("我要卖");

        //添加两个一样的 refreshLayout
        viewList.addLast(getRefreshLayout(R.layout.common_recycler_view));
        viewList.addLast(getRefreshLayout(R.layout.common_recycler_view));

        //viewpage 的 adapter
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

        //给 viewpager 设置标签
        tabIndicator.setViewPagerSwitchSpeed(viewPager, 600);
        tabIndicator.setTabData(viewPager, titleList, new TabIndicator.TabClickListener() {
            @Override
            public void onClick(int position) {
                //顶部点击的方法公布出来
                if (position == 1 && MyUtil.isLoginDeal(getActivity())) {
                    if (!initSellRecycle) {
                        viewList.get(1).startRefresh();
                        initSellRecycle = true;
                    }
                    viewPager.setCurrentItem(position);
                } else if (position == 0) {
                    viewPager.setCurrentItem(position);
                }
            }
        });

        //监听 viewpagere 滑动，没有登陆不允许划到 我要卖列表
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (position == 0 && positionOffset > 0.2) {
                    if (!MyUtil.isLogin()) {
                        viewPager.setCurrentItem(0);
                        startLoginActivity();
                    } else if (!initSellRecycle) {
                        viewList.get(1).startRefresh();
                        initSellRecycle = true;
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    //开启登陆 activity ，不要重复
    long startLoginActivityTime = 0;

    private void startLoginActivity() {
        if (System.currentTimeMillis() - startLoginActivityTime > 2 * 1000) {
            startLoginActivityTime = System.currentTimeMillis();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            getActivity().startActivity(intent);
            ToastUtil.showLongToast("请先登陆");
        }
    }

    /**
     * 根据 xml 拿到 refreshLayout
     */
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
     * 初始化两个 refreshLayout 中的 recyclerview
     */
    private void initRecyclerView() {

        //item分割线
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.shape_market_recycle_view_divider);
        dividerItemDecoration.setDrawable(drawable);


        //初始化我要买
        TwinklingRefreshLayout buyRefreshView = viewList.get(0);
        //监听 下拉刷新 上拉加载
        buyRefreshView.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                refreshData(buyOrders, "2", marketBuyAdapter, refreshLayout);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                loadMoreData(buyOrders, "2", marketBuyAdapter, refreshLayout);
            }
        });

        buyRecyclerView = buyRefreshView.findViewById(R.id.recycleView);
        buyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        buyRecyclerView.setItemAnimator(new DefaultItemAnimator());


        buyRecyclerView.addItemDecoration(dividerItemDecoration);
        //设置 adapter
        marketBuyAdapter = new MarketBuyAdapter(buyOrders, this);
        marketBuyAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //我要买广告详情
                if (MyUtil.isLoginDeal(getActivity())) {
                    String orderCode = buyOrders.get(position).getOrderCode();
                    Intent intent = new Intent(getActivity(), ADOrderDetailActivity.class);
                    intent.putExtra("orderCode", orderCode);
                    startActivity(intent);
                }

            }
        });
        marketBuyAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.buyButton) {
                    //购买设置
                    if (MyUtil.isLoginDeal(getActivity())) {
                        if (SystemPreferences.getInt(Constant.AUTHSTATUS)==2){
                            startBuySettingActivity(buyOrders.get(position));
                        }else{
                            startActivity(new Intent(getActivity(),UploadIDCardInformationActivity.class));
                        }

                    }
                }
            }
        });
        buyRecyclerView.setAdapter(marketBuyAdapter);


        //初始化我要卖
        TwinklingRefreshLayout sellRefreshView = viewList.get(1);
        //监听 下拉刷新 上拉加载
        sellRefreshView.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                refreshData(sellOrders, "1", marketSellAdapter, refreshLayout);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                loadMoreData(sellOrders, "1", marketSellAdapter, refreshLayout);
            }
        });

        sellRecyclerView = sellRefreshView.findViewById(R.id.recycleView);
        sellRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        sellRecyclerView.setItemAnimator(new DefaultItemAnimator());

        sellRecyclerView.addItemDecoration(dividerItemDecoration);
        //设置 adapter
        marketSellAdapter = new MarketSellAdapter(sellOrders, this);
        marketSellAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //出售设置
                if (MyUtil.isLoginDeal(getActivity())) {
                    if (SystemPreferences.getInt(Constant.AUTHSTATUS)==2){
                        startSellSettingActivity(sellOrders.get(position));

                    }else{
                        startActivity(new Intent(getActivity(), UploadIDCardInformationActivity.class));
                    }
                }
            }
        });
        marketSellAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.sellButton) {
                    //出售设置
                    if (MyUtil.isLoginDeal(getActivity())) {

                        if (SystemPreferences.getInt(Constant.AUTHSTATUS)==2){
                            startSellSettingActivity(sellOrders.get(position));

                        }else{
                            startActivity(new Intent(getActivity(), UploadIDCardInformationActivity.class));
                        }
                    }
                }
            }
        });
        sellRecyclerView.setAdapter(marketSellAdapter);
    }

    /**
     * 开启出售设置 activity
     */
    private void startSellSettingActivity(ADOrder aDOrder) {
        if (MyUtil.isLoginDeal(getActivity())) {
            Intent intent = new Intent(getActivity(), SellSettingActivity.class);
            intent.putExtra("orderCode", aDOrder.getOrderCode());
            intent.putExtra("price", aDOrder.getPrice());
            intent.putExtra("productId", aDOrder.getProductId());
            intent.putExtra("orderNumber", aDOrder.getOrderNumber());
            intent.putExtra("saleedNumber", aDOrder.getSaleedNumber());
            intent.putExtra("saleNumber", aDOrder.getSaleNumber());
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.activity_bottom_enter, 0);
        }
    }

    /**
     * 开启购买设置 activity
     */
    private void startBuySettingActivity(ADOrder aDOrder) {
        if (MyUtil.isLoginDeal(getActivity())) {
            Intent intent = new Intent(getActivity(), BuySettingActivity.class);
            intent.putExtra("orderCode", aDOrder.getOrderCode());
            intent.putExtra("price", aDOrder.getPrice());
            intent.putExtra("productId", aDOrder.getProductId());
            intent.putExtra("orderNumber", aDOrder.getOrderNumber());
            intent.putExtra("saleedNumber", aDOrder.getSaleedNumber());
            intent.putExtra("saleNumber", aDOrder.getSaleNumber());
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.activity_bottom_enter, 0);
        }
    }

    /**
     * 刷新数据
     *
     * @param list
     * @param type
     * @param adapter
     * @param refreshLayout
     */
    private void refreshData(final List<ADOrder> list, String type, final BaseQuickAdapter adapter, final TwinklingRefreshLayout refreshLayout) {
        if ("1".equals(type)) {
            int userId = (int) ((MainActivity) getActivity()).getUserId();
            NetFactory.create(ADOrderApi.class).getADOrderList(userId, Constant.pageSize, 1, type).enqueue(new Callback<ResponseData<EntityList<ADOrder>>>() {
                @Override
                public void onResponse(Call<ResponseData<EntityList<ADOrder>>> call, Response<ResponseData<EntityList<ADOrder>>> response) {
                    ResponseData<EntityList<ADOrder>> responseData = response.body();
                    if (responseData.getData() == null || responseData.getStatus() != 200 || responseData.getData().getList() == null || responseData.getData().getList().size() == 0) {
//                        ToastUtil.showLongToast("没有更多数据");
                        refreshLayout.finishRefreshing();
                        return;
                    }
                    list.clear();
                    list.addAll(responseData.getData().getList());
                    adapter.notifyDataSetChanged();
                    refreshLayout.finishRefreshing();
                }

                @Override
                public void onFailure(Call<ResponseData<EntityList<ADOrder>>> call, Throwable t) {
                    ToastUtil.showLongToast("网络错误");
                }
            });
        } else {
            NetFactory.create(ADOrderApi.class).getADOrderList(Constant.pageSize, 1, type).enqueue(new Callback<ResponseData<EntityList<ADOrder>>>() {
                @Override
                public void onResponse(Call<ResponseData<EntityList<ADOrder>>> call, Response<ResponseData<EntityList<ADOrder>>> response) {
                    ResponseData<EntityList<ADOrder>> responseData = response.body();
                    if (responseData.getData() == null || responseData.getStatus() != 200 || responseData.getData().getList() == null || responseData.getData().getList().size() == 0) {
//                        ToastUtil.showLongToast("没有更多数据");
                        refreshLayout.finishRefreshing();
                        return;
                    }
                    list.clear();
                    list.addAll(responseData.getData().getList());
                    adapter.notifyDataSetChanged();
                    refreshLayout.finishRefreshing();
                }

                @Override
                public void onFailure(Call<ResponseData<EntityList<ADOrder>>> call, Throwable t) {
                    ToastUtil.showLongToast("网络错误");
                }
            });
        }

    }

    /**
     * 加载更多
     *
     * @param list
     * @param type
     * @param adapter
     * @param refreshLayout
     */
    private void loadMoreData(final List<ADOrder> list, String type, final BaseQuickAdapter adapter, final TwinklingRefreshLayout refreshLayout) {

        int pageNum = list.size() / Constant.pageSize + 1;
        if (list.size() % Constant.pageSize != 0) {
            pageNum++;
        }

        if ("1".equals(type)) {
            int userId = (int) ((MainActivity) getActivity()).getUserId();
            NetFactory.create(ADOrderApi.class).getADOrderList(userId, Constant.pageSize, pageNum, type).enqueue(new Callback<ResponseData<EntityList<ADOrder>>>() {
                @Override
                public void onResponse(Call<ResponseData<EntityList<ADOrder>>> call, Response<ResponseData<EntityList<ADOrder>>> response) {
                    ResponseData<EntityList<ADOrder>> responseData = response.body();
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
                public void onFailure(Call<ResponseData<EntityList<ADOrder>>> call, Throwable t) {
                    ToastUtil.showLongToast("网络错误");
                }
            });
        } else {
            NetFactory.create(ADOrderApi.class).getADOrderList(Constant.pageSize, pageNum, type).enqueue(new Callback<ResponseData<EntityList<ADOrder>>>() {
                @Override
                public void onResponse(Call<ResponseData<EntityList<ADOrder>>> call, Response<ResponseData<EntityList<ADOrder>>> response) {
                    ResponseData<EntityList<ADOrder>> responseData = response.body();
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
                public void onFailure(Call<ResponseData<EntityList<ADOrder>>> call, Throwable t) {
                    ToastUtil.showLongToast("网络错误");
                }
            });
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //注销 binder
        unbinder.unbind();

        if (markerBroadcast != null){
            getActivity().unregisterReceiver(markerBroadcast);
        }
    }


    private class MarkerBroadcast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int type=intent.getIntExtra("type",0);
            if (type==1){
                viewList.get(0).startRefresh();
            }else if (type==2){
                viewList.get(1).startRefresh();
            }
        }
    }

}
