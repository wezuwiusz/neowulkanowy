package io.github.wulkanowy.di

import android.content.Context
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.WulkanowyApp
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
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
    fun provideJobDispatcher(context: Context) = FirebaseJobDispatcher(GooglePlayDriver(context))

    @Singleton
    @Provides
    fun provideFirebaseAnalyticsHelper(context: Context) = FirebaseAnalyticsHelper(FirebaseAnalytics.getInstance(context))
}
