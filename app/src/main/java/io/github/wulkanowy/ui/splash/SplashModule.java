package io.github.wulkanowy.ui.splash;

import dagger.Binds;
import dagger.Module;
import io.github.wulkanowy.di.scopes.PerActivity;

@Module
public abstract class SplashModule {

    @PerActivity
    @Binds
    abstract SplashContract.Presenter provideSplashPresenter(SplashPresenter splashPresenter);
}
