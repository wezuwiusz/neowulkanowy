package io.github.wulkanowy.ui.modules.message

import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import io.github.wulkanowy.di.scopes.PerChildFragment
import io.github.wulkanowy.di.scopes.PerFragment
import io.github.wulkanowy.ui.base.BaseFragmentPagerAdapter
import io.github.wulkanowy.ui.modules.message.tab.MessageTabFragment

@Suppress("unused")
@Module
abstract class MessageModule {

    @Module
    companion object {

        @JvmStatic
        @PerFragment
        @Provides
        fun provideMessageAdapter(fragment: MessageFragment) = BaseFragmentPagerAdapter(fragment.childFragmentManager)
    }

    @PerChildFragment
    @ContributesAndroidInjector
    abstract fun bindMessageTabFragment(): MessageTabFragment
}
