package org.eq.android.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;

import org.eq.android.R;
import org.eq.android.fargment.MarketFragment;
import org.eq.android.fargment.MineFragment;
import org.eq.android.fargment.TicketFragment;
import org.eq.android.utils.MyUtil;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    private Fragment marketFragment;
    private Fragment ticketFragment;
    private Fragment mineFragment;
    private Fragment currentFragment;

    @BindView(R.id.nav_view)
    BottomNavigationView navView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_market:
                    if (marketFragment == null) marketFragment = MarketFragment.newInstance();
                    switchFragment(marketFragment);
                    return true;
                case R.id.navigation_ticket:
                    if (MyUtil.isLoginDeal(MainActivity.this)) {
                        if (ticketFragment == null) ticketFragment = TicketFragment.newInstance();
                        switchFragment(ticketFragment);
                    }

                    return true;
                case R.id.navigation_mine:
                    if (mineFragment == null) mineFragment = MineFragment.newInstance("我的");
                    switchFragment(mineFragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    public int getContentViewResId() {
        return R.layout.activity_main;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        makeStatusBarTransprant();


        //添加第一个 fragment
        marketFragment = MarketFragment.newInstance();
        switchFragment(marketFragment);

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


    /**
     * 切换 fragment
     */
    private void switchFragment(Fragment fragment) {
        if (currentFragment == fragment)
            return;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment);
        }
        if (!fragment.isAdded()) {
            fragmentTransaction.add(R.id.container_fragment, fragment);
        } else {
            fragmentTransaction.show(fragment);
        }
        fragmentTransaction.commit();
        currentFragment = fragment;
    }
}
