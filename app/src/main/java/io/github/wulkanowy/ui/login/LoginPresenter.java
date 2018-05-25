package io.github.wulkanowy.ui.login;

import android.text.TextUtils;

import java.util.LinkedHashMap;

import javax.inject.Inject;

import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.ui.base.BasePresenter;
import io.github.wulkanowy.utils.AppConstant;
import io.github.wulkanowy.utils.FabricUtils;

public class LoginPresenter extends BasePresenter<LoginContract.View>
        implements LoginContract.Presenter {

    private LoginTask loginAsync;

    private String email;

    private String password;

    private String symbol;

    @Inject
    LoginPresenter(RepositoryContract repository) {
        super(repository);
    }

    @Override
    public void attemptLogin(String email, String password, String symbol) {
        getView().resetViewErrors();

        this.email = email;
        this.password = password;
        this.symbol = getNormalizedSymbol(symbol);

        if (!isAllFieldCorrect(password, email)) {
            getView().showSoftInput();
            return;
        }

        if (getView().isNetworkConnected()) {
            loginAsync = new LoginTask(this);
            loginAsync.execute();

        } else {
            getView().onNoNetworkError();
        }

        getView().hideSoftInput();
    }

    @Override
    public void onStartAsync() {
        if (isViewAttached()) {
            getView().showActionBar(false);
            getView().showLoginProgress(true);
        }
    }

    @Override
    public void onDoInBackground(int stepNumber) throws Exception {
        switch (stepNumber) {
            case 1:
                getRepository().getSyncRepo().registerUser(email, password, symbol);
                break;
            case 2:
                getRepository().getSyncRepo().syncAll();
                break;
        }
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
            FabricUtils.logRegister(true, getRepository().getDbRepo().getCurrentSymbol().getSymbol());
            getView().openMainActivity();
            return;
        } else if (exception instanceof BadCredentialsException) {
            getView().setErrorPassIncorrect();
            getView().showSoftInput();
        } else if (exception instanceof AccountPermissionException) {
            getView().setErrorSymbolRequired();
            getView().showSoftInput();
        } else {
            FabricUtils.logRegister(false, symbol);
            getView().onError(getRepository().getResRepo().getErrorLoginMessage(exception));
        }

        getView().showActionBar(true);
        getView().showLoginProgress(false);
    }

    @Override
    public void onCanceledAsync() {
        if (isViewAttached()) {
            getView().showActionBar(true);
            getView().showLoginProgress(false);
        }
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

        String[] keys = getRepository().getResRepo().getSymbolsKeysArray();
        String[] values = getRepository().getResRepo().getSymbolsValuesArray();
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

    @Override
    public void onDestroy() {
        if (loginAsync != null) {
            loginAsync.cancel(true);
            loginAsync = null;
        }
        super.onDestroy();
    }
}
