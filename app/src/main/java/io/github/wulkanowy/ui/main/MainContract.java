package io.github.wulkanowy.ui.main;

import io.github.wulkanowy.di.annotations.PerActivity;
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

    @PerActivity
    interface Presenter extends BaseContract.Presenter<View> {

        void onStart(View view, int tabPositionIntent);

        void onTabSelected(int position, boolean wasSelected);

        void onFragmentIsReady();
    }
}
