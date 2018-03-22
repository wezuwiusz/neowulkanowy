package io.github.wulkanowy.ui.main.attendance;

import io.github.wulkanowy.di.annotations.PerActivity;
import io.github.wulkanowy.ui.base.BaseContract;
import io.github.wulkanowy.ui.main.OnFragmentIsReadyListener;
import io.github.wulkanowy.ui.main.TabsData;

public interface AttendanceContract {

    interface View extends BaseContract.View {

        void setActivityTitle();

        void scrollViewPagerToPosition(int position);

        void setTabDataToAdapter(TabsData tabsData);

        void setAdapterWithTabLayout();

        boolean isMenuVisible();
    }

    @PerActivity
    interface Presenter extends BaseContract.Presenter<View> {

        void onFragmentActivated(boolean isVisible);

        void onStart(View view, OnFragmentIsReadyListener listener);

        void setRestoredPosition(int position);
    }
}
