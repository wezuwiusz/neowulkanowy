package io.github.wulkanowy.di

import android.appwidget.AppWidgetManager
import android.content.Context
import com.yariksoffice.lingver.Lingver
import dagger.Module
import dagger.Provides
import io.github.wulkanowy.WulkanowyApp
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.DispatchersProvider
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

    @Singleton
    @Provides
    fun provideDispatchersProvider() = DispatchersProvider()

    @Singleton
    @Provides
    fun provideAppWidgetManager(context: Context): AppWidgetManager = AppWidgetManager.getInstance(context)

    @Singleton
    @Provides
    fun provideAppInfo() = AppInfo()

    @Singleton
    @Provides
    fun provideLingver() = Lingver.getInstance()
}
