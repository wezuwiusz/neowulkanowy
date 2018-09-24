package io.github.wulkanowy.di

import android.content.Context
import dagger.Module
import dagger.Provides
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.WulkanowyApp
import io.github.wulkanowy.utils.schedulers.SchedulersManager
import io.github.wulkanowy.utils.schedulers.SchedulersProvider

@Module
internal class AppModule {

    @Provides
    fun provideContext(app: WulkanowyApp): Context = app

    @Provides
    fun provideSchedulers(): SchedulersManager = SchedulersProvider()

    @Provides
    fun provideFlexibleAdapter() = FlexibleAdapter<AbstractFlexibleItem<*>>(null, null, true)
}
