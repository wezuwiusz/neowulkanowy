package io.github.wulkanowy.di

import android.content.Context
import dagger.Module
import dagger.Provides
import io.github.wulkanowy.WulkanowyApp
import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.utils.schedulers.SchedulersManager
import io.github.wulkanowy.utils.schedulers.SchedulersProvider

@Module
internal class AppModule {

    @Provides
    fun provideContext(app: WulkanowyApp): Context = app

    @Provides
    fun provideSchedulers(): SchedulersManager = SchedulersProvider()

    @Provides
    fun provideErrorHandler(context: Context): ErrorHandler = ErrorHandler(context.resources)
}
