package io.github.wulkanowy.ui.login.options

import dagger.Module
import dagger.Provides
import eu.davidea.flexibleadapter.FlexibleAdapter
import io.github.wulkanowy.di.scopes.PerFragment

@Module
internal class LoginOptionsModule {

    @Provides
    @PerFragment
    fun provideLoginOptionsAdapter() = FlexibleAdapter<LoginOptionsItem>(null)
}