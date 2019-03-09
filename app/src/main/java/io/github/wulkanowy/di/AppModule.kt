package io.github.wulkanowy.di

import android.appwidget.AppWidgetManager
import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.BuildConfig.DEBUG
import io.github.wulkanowy.WulkanowyApp
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import javax.inject.Named
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
    fun provideFirebaseAnalyticsHelper(context: Context) = FirebaseAnalyticsHelper(FirebaseAnalytics.getInstance(context))

    @Singleton
    @Provides
    fun provideAppWidgetManager(context: Context) = AppWidgetManager.getInstance(context)

    @Singleton
    @Named("isDebug")
    @Provides
    fun provideIsDebug() = DEBUG
}
