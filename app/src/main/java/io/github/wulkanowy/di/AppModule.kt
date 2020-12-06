package io.github.wulkanowy.di

import android.appwidget.AppWidgetManager
import android.content.Context
import com.yariksoffice.lingver.Lingver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.wulkanowy.utils.DispatchersProvider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class AppModule {

    @Singleton
    @Provides
    fun provideDispatchersProvider() = DispatchersProvider()

    @Singleton
    @Provides
    fun provideAppWidgetManager(@ApplicationContext context: Context): AppWidgetManager = AppWidgetManager.getInstance(context)

    @Singleton
    @Provides
    fun provideLingver() = Lingver.getInstance()
}
