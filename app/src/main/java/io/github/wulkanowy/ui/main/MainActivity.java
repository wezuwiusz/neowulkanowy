package io.github.wulkanowy.ui.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.wulkanowy.R;
import io.github.wulkanowy.ui.base.BaseActivity;
import io.github.wulkanowy.ui.main.attendance.AttendanceFragment;
import io.github.wulkanowy.ui.main.dashboard.DashboardFragment;
import io.github.wulkanowy.ui.main.grades.GradesFragment;
import io.github.wulkanowy.ui.main.settings.SettingsFragment;
import io.github.wulkanowy.ui.main.timetable.TimetableFragment;

public class MainActivity extends BaseActivity implements MainContract.View,
        AHBottomNavigation.OnTabSelectedListener, OnFragmentIsReadyListener {

    public static final String EXTRA_CARD_ID_KEY = "cardId";

    @BindView(R.id.main_activity_nav)
    AHBottomNavigation bottomNavigation;

    @BindView(R.id.main_activity_view_pager)
    AHBottomNavigationViewPager viewPager;

    @BindView(R.id.main_activity_progress_bar)
    View progressBar;

    @Inject
    MainPagerAdapter pagerAdapter;

    @Inject
    MainContract.Presenter presenter;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        getActivityComponent().inject(this);
        setButterKnife(ButterKnife.bind(this));

        presenter.onStart(this, getIntent().getIntExtra(EXTRA_CARD_ID_KEY, -1));
    }

    @Override
    public void showProgressBar(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        viewPager.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        bottomNavigation.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void showActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
    }

    @Override
    public void hideActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @Override
    public boolean onTabSelected(int position, boolean wasSelected) {
        presenter.onTabSelected(position, wasSelected);
        return true;
    }

    @Override
    public void setCurrentPage(int position) {
        viewPager.setCurrentItem(position, false);
    }

    @Override
    public void onFragmentIsReady() {
        presenter.onFragmentIsReady();
    }

    @Override
    public void initiationBottomNav(int tabPosition) {
        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.grades_text),
                R.drawable.ic_menu_grade_26dp));

        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.attendance_text),
                R.drawable.ic_menu_attendance_24dp));

        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.dashboard_text),
                R.drawable.ic_menu_dashboard_24dp));

        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.timetable_text),
                R.drawable.ic_menu_timetable_24dp));

        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.settings_text),
                R.drawable.ic_menu_other_24dp));

        bottomNavigation.setAccentColor(getResources().getColor(R.color.colorPrimary));
        bottomNavigation.setInactiveColor(Color.BLACK);
        bottomNavigation.setBackgroundColor(getResources().getColor(R.color.colorBackgroundBottomNav));
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bottomNavigation.setOnTabSelectedListener(this);
        bottomNavigation.setCurrentItem(tabPosition);
        bottomNavigation.setBehaviorTranslationEnabled(false);
    }

    @Override
    public void initiationViewPager(int tabPosition) {
        pagerAdapter.addFragment(new GradesFragment());
        pagerAdapter.addFragment(new AttendanceFragment());
        pagerAdapter.addFragment(new DashboardFragment());
        pagerAdapter.addFragment(new TimetableFragment());
        pagerAdapter.addFragment(new SettingsFragment());

        viewPager.setPagingEnabled(false);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setCurrentItem(tabPosition, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}
