package io.github.wulkanowy.di.modules;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import dagger.Module;
import dagger.Provides;
import io.github.wulkanowy.di.annotations.ActivityContext;
import io.github.wulkanowy.di.annotations.PerActivity;
import io.github.wulkanowy.ui.base.BasePagerAdapter;
import io.github.wulkanowy.ui.login.LoginContract;
import io.github.wulkanowy.ui.login.LoginPresenter;
import io.github.wulkanowy.ui.main.MainContract;
import io.github.wulkanowy.ui.main.MainPresenter;
import io.github.wulkanowy.ui.splash.SplashContract;
import io.github.wulkanowy.ui.splash.SplashPresenter;

@Module
public class ActivityModule {

    private AppCompatActivity activity;

    public ActivityModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @ActivityContext
    @Provides
    Context provideContext() {
        return activity;
    }

    @Provides
    AppCompatActivity provideActivity() {
        return activity;
    }

    @PerActivity
    @Provides
    SplashContract.Presenter provideSplashPresenter
            (SplashPresenter splashPresenter) {
        return splashPresenter;
    }

    @PerActivity
    @Provides
    LoginContract.Presenter provideLoginPresenter
            (LoginPresenter loginPresenter) {
        return loginPresenter;
    }

    @PerActivity
    @Provides
    MainContract.Presenter provideMainPresenter
            (MainPresenter mainPresenter) {
        return mainPresenter;
    }

    @Provides
    BasePagerAdapter provideMainPagerAdapter() {
        return new BasePagerAdapter(activity.getSupportFragmentManager());
    }
}
