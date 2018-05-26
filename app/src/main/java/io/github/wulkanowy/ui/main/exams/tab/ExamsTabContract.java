package io.github.wulkanowy.ui.main.exams.tab;

import java.util.List;

import io.github.wulkanowy.ui.base.BaseContract;

public interface ExamsTabContract {

    interface View extends BaseContract.View {

        void onRefreshSuccess();

        void hideRefreshingBar();

        void showNoItem(boolean show);

        void showProgressBar(boolean show);

        void updateAdapterList(List<ExamsSubItem> headerItems);
    }

    interface Presenter extends BaseContract.Presenter<View> {

        void onFragmentActivated(boolean isSelected);

        void setArgumentDate(String date);

        void onRefresh();
    }
}
