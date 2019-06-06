package io.github.wulkanowy.di

import android.appwidget.AppWidgetManager
import android.content.Context
import dagger.Module
import dagger.Provides
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.WulkanowyApp
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.SchedulersProvider
import javax.inject.Singleton

@Module
internal class AppModule {

    @Singleton
    @Provides
    fun provideContext(app: WulkanowyApp): Context = app

    @Singleton
    @Provides
    fun provideSchedulersProvider() = SchedulersProvider()

    @Provides
    fun provideFlexibleAdapter() = FlexibleAdapter<AbstractFlexibleItem<*>>(null, null, true)

    @Singleton
    @Provides
    fun provideAppWidgetManager(context: Context): AppWidgetManager = AppWidgetManager.getInstance(context)

    @Singleton
    @Provides
    fun provideAppInfo() = AppInfo()
}
