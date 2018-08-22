package io.github.wulkanowy.di

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import io.github.wulkanowy.WulkanowyApp
import io.github.wulkanowy.data.RepositoryModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class,
    RepositoryModule::class,
    BuilderModule::class])
interface AppComponent : AndroidInjector<WulkanowyApp> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<WulkanowyApp>()
}
