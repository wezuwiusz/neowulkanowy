package io.github.wulkanowy.ui.main.attendance;

import android.support.annotation.NonNull;

import io.github.wulkanowy.ui.base.BaseContract;
import io.github.wulkanowy.ui.main.OnFragmentIsReadyListener;

public interface AttendanceContract {

    interface View extends BaseContract.View {

        void setActivityTitle();

        void scrollViewPagerToPosition(int position);

        void setTabDataToAdapter(String date);

        void setAdapterWithTabLayout();

        boolean isMenuVisible();

        void setThemeForTab(int position);
    }

    interface Presenter extends BaseContract.Presenter<View> {

        void onFragmentActivated(boolean isVisible);

        void attachView(@NonNull View view, OnFragmentIsReadyListener listener);

        void setRestoredPosition(int position);
    }
}
