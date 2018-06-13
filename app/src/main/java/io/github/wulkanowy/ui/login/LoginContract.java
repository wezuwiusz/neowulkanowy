package io.github.wulkanowy.ui.login;

import io.github.wulkanowy.ui.base.BaseContract;

public interface LoginContract {
    interface View extends BaseContract.View {

        void setErrorEmailRequired();

        void setErrorPassRequired();

        void setErrorSymbolRequired();

        void setErrorEmailInvalid();

        void setErrorPassInvalid();

        void setErrorPassIncorrect();

        void resetViewErrors();

        void setStepOneLoginProgress();

        void setStepTwoLoginProgress();

        void openMainActivity();

        void showLoginProgress(boolean show);

        void showSoftInput();

        void hideSoftInput();

        void showActionBar(boolean show);

        void onSyncFailed();

    }

    interface Presenter extends BaseContract.Presenter<View> {

        void attemptLogin(String email, String password, String symbol);

        void onStartAsync();

        void onDoInBackground(int stepNumber) throws Exception;

        void onLoginProgress(int step);

        void onEndAsync(int success, Exception exception);

        void onCanceledAsync();
    }
}
