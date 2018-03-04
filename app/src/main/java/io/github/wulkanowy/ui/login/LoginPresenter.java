package io.github.wulkanowy.ui.login;

import android.text.TextUtils;

import java.util.LinkedHashMap;

import javax.inject.Inject;

import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.ui.base.BasePresenter;
import io.github.wulkanowy.utils.AppConstant;

public class LoginPresenter extends BasePresenter<LoginContract.View>
        implements LoginContract.Presenter {

    private LoginTask loginAsync;

    @Inject
    LoginPresenter(RepositoryContract repository) {
        super(repository);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (loginAsync != null) {
            loginAsync.cancel(true);
            loginAsync = null;
        }
    }

    @Override
    public void attemptLogin(String email, String password, String symbol) {
        getView().resetViewErrors();

        if (!isAllFieldCorrect(password, email)) {
            getView().showSoftInput();
            return;
        }

        if (getView().isNetworkConnected()) {
            // Dopóki używamy AsyncTask presenter będzie musiał "wiedzieć" o AsyncTaskach
            loginAsync = new LoginTask(this,
                    email,
                    password,
                    getNormalizedSymbol(symbol));
            loginAsync.execute();

        } else {
            getView().onNoNetworkError();
        }

        getView().hideSoftInput();
    }

    @Override
    public void onStartAsync() {
        getView().showLoginProgress(true);
    }

    @Override
    public void onLoginProgress(int step) {
        if (step == 1) {
            getView().setStepOneLoginProgress();
        } else if (step == 2) {
            getView().setStepTwoLoginProgress();
        }
    }

    @Override
    public void onEndAsync(boolean success, Exception exception) {
        if (success) {
            getView().openMainActivity();
        } else if (exception instanceof BadCredentialsException) {
            getView().setErrorPassIncorrect();
            getView().showSoftInput();
            getView().showLoginProgress(false);
        } else if (exception instanceof AccountPermissionException) {
            getView().setErrorSymbolRequired();
            getView().showSoftInput();
            getView().showLoginProgress(false);
        } else {
            getView().onError(getRepository().getErrorLoginMessage(exception));
            getView().showLoginProgress(false);
        }

    }

    @Override
    public void onCanceledAsync() {
        getView().showLoginProgress(false);
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") || email.contains("\\\\");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private String getNormalizedSymbol(String symbol) {
        if (TextUtils.isEmpty(symbol)) {
            return AppConstant.DEFAULT_SYMBOL;
        }

        String[] keys = getRepository().getSymbolsKeysArray();
        String[] values = getRepository().getSymbolsValuesArray();
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        for (int i = 0; i < Math.min(keys.length, values.length); ++i) {
            map.put(keys[i], values[i]);
        }

        if (map.containsKey(symbol)) {
            return map.get(symbol);
        }
        return AppConstant.DEFAULT_SYMBOL;
    }

    private boolean isAllFieldCorrect(String password, String email) {
        boolean correct = true;

        if (TextUtils.isEmpty(password)) {
            getView().setErrorPassRequired();
            correct = false;
        } else if (!isPasswordValid(password)) {
            getView().setErrorPassInvalid();
            correct = false;
        }

        if (TextUtils.isEmpty(email)) {
            getView().setErrorEmailRequired();
            correct = false;
        } else if (!isEmailValid(email)) {
            getView().setErrorEmailInvalid();
            correct = false;
        }
        return correct;
    }
}
