package io.github.wulkanowy.ui.main.timetable;

import java.util.List;

import io.github.wulkanowy.ui.base.BaseContract;

public interface TimetableTabContract {

    interface View extends BaseContract.View {

        void updateAdapterList(List<TimetableHeaderItem> headerItems);

        void onRefreshSuccess();

        void hideRefreshingBar();

        void showNoItem(boolean show);

        void showProgressBar(boolean show);

        void setFreeWeekName(String text);
    }

    interface Presenter extends BaseContract.Presenter<View> {

        void onFragmentSelected(boolean isSelected);

        void setArgumentDate(String date);

        void onStart(View view, boolean isPrimary);

        void onRefresh();
    }
}
