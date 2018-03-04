package io.github.wulkanowy.ui.main;

import io.github.wulkanowy.di.annotations.PerActivity;
import io.github.wulkanowy.ui.base.BaseContract;

public interface MainContract {

    interface View extends BaseContract.View {

        void setCurrentPage(int position);

        void showProgressBar(boolean show);

        void showActionBar();

        void hideActionBar();
    }

    @PerActivity
    interface Presenter extends BaseContract.Presenter<View> {

        void onTabSelected(int position, boolean wasSelected);

        void onFragmentIsReady();
    }
}
