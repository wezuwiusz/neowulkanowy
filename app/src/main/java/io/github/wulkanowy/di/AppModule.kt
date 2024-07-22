package io.github.wulkanowy.di

import android.appwidget.AppWidgetManager
import android.content.Context
import com.yariksoffice.lingver.Lingver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.internal.Provider
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

    @Provides
    fun provideAppWidgetManagerProvider(@ApplicationContext context: Context): Provider<AppWidgetManager> {
        return Provider { context.getSystemService(Context.APPWIDGET_SERVICE) as AppWidgetManager }
    }

    @Singleton
    @Provides
    fun provideLingver() = Lingver.getInstance()
}
