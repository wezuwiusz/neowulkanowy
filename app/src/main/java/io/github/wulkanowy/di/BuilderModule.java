package io.github.wulkanowy.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.github.wulkanowy.di.scopes.PerActivity;
import io.github.wulkanowy.services.jobs.SyncJob;
import io.github.wulkanowy.services.widgets.TimetableWidgetServices;
import io.github.wulkanowy.ui.login.LoginActivity;
import io.github.wulkanowy.ui.login.LoginModule;
import io.github.wulkanowy.ui.main.MainActivity;
import io.github.wulkanowy.ui.main.MainModule;
import io.github.wulkanowy.ui.splash.SplashActivity;
import io.github.wulkanowy.ui.splash.SplashModule;
import io.github.wulkanowy.ui.widgets.TimetableWidgetProvider;

@Module
abstract class BuilderModule {

    @PerActivity
    @ContributesAndroidInjector(modules = SplashModule.class)
    abstract SplashActivity bindSplashActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = LoginModule.class)
    abstract LoginActivity bindLoginActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = MainModule.class)
    abstract MainActivity bindMainActivity();

    @ContributesAndroidInjector
    abstract SyncJob bindSyncJob();

    @ContributesAndroidInjector
    abstract TimetableWidgetServices bindTimetableWidgetServices();

    @ContributesAndroidInjector
    abstract TimetableWidgetProvider bindTimetableWidgetProvider();
}
