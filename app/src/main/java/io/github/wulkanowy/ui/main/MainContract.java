package io.github.wulkanowy.ui.main;

import android.support.annotation.NonNull;

import io.github.wulkanowy.ui.base.BaseContract;

public interface MainContract {

    interface View extends BaseContract.View {

        void setCurrentPage(int position);

        void showProgressBar(boolean show);

        void showActionBar();

        void hideActionBar();

        void initiationViewPager(int tabPosition);

        void initiationBottomNav(int tabPosition);

        void startSyncService(int interval, boolean useOnlyWifi);
    }

    interface Presenter extends BaseContract.Presenter<View> {

        void attachView(@NonNull View view, int initTabId);

        void onTabSelected(int position, boolean wasSelected);

        void onFragmentIsReady();
    }
}
