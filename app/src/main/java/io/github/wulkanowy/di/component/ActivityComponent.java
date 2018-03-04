package io.github.wulkanowy.di.component;

import dagger.Component;
import io.github.wulkanowy.di.annotations.PerActivity;
import io.github.wulkanowy.di.modules.ActivityModule;
import io.github.wulkanowy.ui.login.LoginActivity;
import io.github.wulkanowy.ui.main.MainActivity;
import io.github.wulkanowy.ui.splash.SplashActivity;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(SplashActivity splashActivity);

    void inject(LoginActivity loginActivity);

    void inject(MainActivity mainActivity);
}
