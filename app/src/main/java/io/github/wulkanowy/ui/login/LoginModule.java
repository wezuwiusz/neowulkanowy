package io.github.wulkanowy.ui.login;

import dagger.Binds;
import dagger.Module;
import io.github.wulkanowy.di.scopes.PerActivity;

@Module
public abstract class LoginModule {

    @PerActivity
    @Binds
    abstract LoginContract.Presenter provideLoginPresenter(LoginPresenter loginPresenter);
}
