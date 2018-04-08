package io.github.wulkanowy.ui.splash;


import io.github.wulkanowy.di.annotations.PerActivity;
import io.github.wulkanowy.ui.base.BaseContract;

public interface SplashContract {

    interface View extends BaseContract.View {

        void openLoginActivity();

        void openMainActivity();

        void startSyncService(int interval, boolean useOnlyWifi);

        void cancelNotifications();
    }

    @PerActivity
    interface Presenter extends BaseContract.Presenter<View> {
    }
}
