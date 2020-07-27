package io.github.wulkanowy.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.github.wulkanowy.di.scopes.PerActivity
import io.github.wulkanowy.services.alarm.TimetableNotificationReceiver
import io.github.wulkanowy.ui.base.ErrorDialog
import io.github.wulkanowy.ui.modules.login.LoginActivity
import io.github.wulkanowy.ui.modules.login.LoginModule
import io.github.wulkanowy.ui.modules.luckynumberwidget.LuckyNumberWidgetConfigureActivity
import io.github.wulkanowy.ui.modules.luckynumberwidget.LuckyNumberWidgetProvider
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainModule
import io.github.wulkanowy.ui.modules.message.send.SendMessageActivity
import io.github.wulkanowy.ui.modules.splash.SplashActivity
import io.github.wulkanowy.ui.modules.timetablewidget.TimetableWidgetConfigureActivity
import io.github.wulkanowy.ui.modules.timetablewidget.TimetableWidgetProvider

@Suppress("unused")
@Module
internal abstract class BindingModule {

    @ContributesAndroidInjector
    abstract fun bindErrorDialog(): ErrorDialog

    @PerActivity
    @ContributesAndroidInjector
    abstract fun bindSplashActivity(): SplashActivity

    @PerActivity
    @ContributesAndroidInjector(modules = [LoginModule::class])
    abstract fun bindLoginActivity(): LoginActivity

    @PerActivity
    @ContributesAndroidInjector(modules = [MainModule::class])
    abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun bindMessageSendActivity(): SendMessageActivity

    @ContributesAndroidInjector
    abstract fun bindTimetableWidgetAccountActivity(): TimetableWidgetConfigureActivity

    @ContributesAndroidInjector
    abstract fun bindTimetableWidgetProvider(): TimetableWidgetProvider

    @ContributesAndroidInjector
    abstract fun bindLuckyNumberWidgetAccountActivity(): LuckyNumberWidgetConfigureActivity

    @ContributesAndroidInjector
    abstract fun bindLuckyNumberWidgetProvider(): LuckyNumberWidgetProvider

    @ContributesAndroidInjector
    abstract fun bindTimetableNotificationReceiver(): TimetableNotificationReceiver
}
