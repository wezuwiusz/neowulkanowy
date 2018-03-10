package io.github.wulkanowy.ui.main.attendance;

import java.util.List;

import io.github.wulkanowy.ui.base.BaseContract;

public interface AttendanceTabContract {

    interface View extends BaseContract.View {

        void updateAdapterList(List<AttendanceHeaderItem> headerItems);

        void onRefreshSuccess();

        void hideRefreshingBar();

        void showNoItem(boolean show);

        void showProgressBar(boolean show);
    }

    interface Presenter extends BaseContract.Presenter<AttendanceTabContract.View> {

        void onFragmentSelected(boolean isSelected);

        void setArgumentDate(String date);

        void onStart(AttendanceTabContract.View view, boolean isPrimary);

        void onRefresh();
    }
}
