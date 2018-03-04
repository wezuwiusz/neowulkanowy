package io.github.wulkanowy.ui.base;

import android.support.annotation.StringRes;

import io.github.wulkanowy.di.annotations.PerActivity;

public interface BaseContract {

    interface View {

        void onError(@StringRes int resId);

        void onError(String message);

        void onNoNetworkError();

        boolean isNetworkConnected();
    }

    @PerActivity
    interface Presenter<V extends View> {

        void onStart(V view);

        void onDestroy();
    }
}
