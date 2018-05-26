package io.github.wulkanowy.ui.base;

import android.support.annotation.NonNull;

public interface BaseContract {

    interface View {

        void showMessage(@NonNull String text);

        void showNoNetworkMessage();

        boolean isNetworkConnected();
    }

    interface Presenter<V extends View> {

        void attachView(@NonNull V view);

        void detachView();
    }
}
