package io.github.wulkanowy.ui.login

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import io.github.wulkanowy.di.scopes.PerActivity
import io.github.wulkanowy.di.scopes.PerFragment
import io.github.wulkanowy.ui.base.BasePagerAdapter
import io.github.wulkanowy.ui.login.form.LoginFormFragment
import io.github.wulkanowy.ui.login.options.LoginOptionsFragment
import io.github.wulkanowy.ui.login.options.LoginOptionsModule
import javax.inject.Named

@Module
internal abstract class LoginModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Named("Login")
        fun provideLoginAdapter(activity: LoginActivity) = BasePagerAdapter(activity.supportFragmentManager)

        @JvmStatic
        @PerActivity
        @Provides
        fun provideLoginErrorHandler(context: Context) = LoginErrorHandler(context.resources)
    }

    @PerFragment
    @ContributesAndroidInjector()
    abstract fun bindLoginFormFragment(): LoginFormFragment

    @PerFragment
    @ContributesAndroidInjector(modules = [LoginOptionsModule::class])
    abstract fun bindLoginOptionsFragment(): LoginOptionsFragment
}
