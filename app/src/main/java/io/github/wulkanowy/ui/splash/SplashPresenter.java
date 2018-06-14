package io.github.wulkanowy.ui.splash;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.ui.base.BasePresenter;

public class SplashPresenter extends BasePresenter<SplashContract.View>
        implements SplashContract.Presenter {

    @Inject
    SplashPresenter(RepositoryContract repository) {
        super(repository);
    }

    @Override
    public void attachView(@NonNull SplashContract.View view) {
        super.attachView(view);
        getView().setCurrentThemeMode(getRepository().getSharedRepo().getCurrentTheme());
        getView().cancelNotifications();

        if (getRepository().getSharedRepo().isUserLoggedIn()) {
            getView().openMainActivity();
        } else {
            getView().openLoginActivity();
        }
    }
}
