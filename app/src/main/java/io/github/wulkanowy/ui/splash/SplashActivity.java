package io.github.wulkanowy.ui.splash;

import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;

import javax.inject.Inject;

import io.github.wulkanowy.services.notifies.NotificationService;
import io.github.wulkanowy.ui.base.BaseActivity;
import io.github.wulkanowy.ui.login.LoginActivity;
import io.github.wulkanowy.ui.main.MainActivity;

public class SplashActivity extends BaseActivity implements SplashContract.View {

    @Inject
    SplashContract.Presenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter.attachView(this);
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    @Override
    public void openLoginActivity() {
        startActivity(LoginActivity.getStartIntent(this));
        finish();
    }

    @Override
    public void openMainActivity() {
        startActivity(MainActivity.getStartIntent(this));
        finish();
    }

    public void setCurrentThemeMode(int mode) {
        AppCompatDelegate.setDefaultNightMode(mode);
    }

    @Override
    public void cancelNotifications() {
        new NotificationService(getApplicationContext()).cancelAll();
    }
}
