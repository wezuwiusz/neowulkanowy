package io.github.wulkanowy.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import io.github.wulkanowy.R;
import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.services.jobs.SyncJob;
import io.github.wulkanowy.ui.base.BaseActivity;
import io.github.wulkanowy.ui.base.BasePagerAdapter;
import io.github.wulkanowy.ui.main.attendance.AttendanceFragment;
import io.github.wulkanowy.ui.main.exams.ExamsFragment;
import io.github.wulkanowy.ui.main.grades.GradesFragment;
import io.github.wulkanowy.ui.main.settings.SettingsFragment;
import io.github.wulkanowy.ui.main.timetable.TimetableFragment;
import io.github.wulkanowy.utils.CommonUtils;

public class MainActivity extends BaseActivity implements MainContract.View,
        AHBottomNavigation.OnTabSelectedListener, OnFragmentIsReadyListener {

    public static final String EXTRA_CARD_ID_KEY = "cardId";

    @BindView(R.id.main_activity_nav)
    AHBottomNavigation bottomNavigation;

    @BindView(R.id.main_activity_view_pager)
    AHBottomNavigationViewPager viewPager;

    @BindView(R.id.main_activity_progress_bar)
    View progressBar;

    @BindView(R.id.main_activity_appbar)
    AppBarLayout appBar;

    @Named("Main")
    @Inject
    BasePagerAdapter pagerAdapter;

    @Inject
    MainContract.Presenter presenter;

    @Inject
    RepositoryContract repository;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.main_activity_toolbar));
        injectViews();

        presenter.attachView(this, getIntent().getIntExtra(EXTRA_CARD_ID_KEY, -1));
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
        appBar.setExpanded(true, true);
        invalidateOptionsMenu();
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

        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.exams_text),
                R.drawable.ic_menu_exams_24dp));

        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.timetable_text),
                R.drawable.ic_menu_timetable_24dp));

        bottomNavigation.addItem(new AHBottomNavigationItem(getString(R.string.settings_text),
                R.drawable.ic_menu_other_24dp));

        bottomNavigation.setAccentColor(getResources().getColor(R.color.colorPrimary));
        bottomNavigation.setInactiveColor(CommonUtils.getThemeAttrColor(this, android.R.attr.textColorTertiary));
        bottomNavigation.setDefaultBackgroundColor(CommonUtils.getThemeAttrColor(this, R.attr.bottomNavBackground));
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bottomNavigation.setOnTabSelectedListener(this);
        bottomNavigation.setCurrentItem(tabPosition);
        bottomNavigation.setBehaviorTranslationEnabled(false);
    }

    @Override
    public void initiationViewPager(int tabPosition) {
        pagerAdapter.addFragment(new GradesFragment());
        pagerAdapter.addFragment(new AttendanceFragment());
        pagerAdapter.addFragment(new ExamsFragment());
        pagerAdapter.addFragment(new TimetableFragment());
        pagerAdapter.addFragment(new SettingsFragment());

        viewPager.setPagingEnabled(false);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setCurrentItem(tabPosition, false);
    }

    @Override
    public void startSyncService(int interval, boolean useOnlyWifi) {
        SyncJob.start(getApplicationContext(), interval, useOnlyWifi);
    }

    @NonNull
    @Override
    protected View getMessageView() {
        return findViewById(R.id.main_activity_view_pager);
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }
}
