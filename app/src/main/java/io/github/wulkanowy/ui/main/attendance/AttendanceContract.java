package io.github.wulkanowy.ui.main.attendance;

import io.github.wulkanowy.di.annotations.PerFragment;
import io.github.wulkanowy.ui.base.BaseContract;
import io.github.wulkanowy.ui.main.OnFragmentIsReadyListener;

public interface AttendanceContract {

    interface View extends BaseContract.View {

        void setActivityTitle();

        boolean isMenuVisible();
    }

    @PerFragment
    interface Presenter extends BaseContract.Presenter<View> {

        void onStart(View view, OnFragmentIsReadyListener listener);

        void onFragmentVisible(boolean isVisible);
    }
}
