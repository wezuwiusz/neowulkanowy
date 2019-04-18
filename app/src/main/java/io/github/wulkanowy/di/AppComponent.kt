package io.github.wulkanowy.di

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import io.github.wulkanowy.WulkanowyApp
import io.github.wulkanowy.data.RepositoryModule
import io.github.wulkanowy.services.ServicesModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class,
    RepositoryModule::class,
    ServicesModule::class,
    BuilderModule::class])
interface AppComponent : AndroidInjector<WulkanowyApp> {

    @Component.Factory
    interface Factory : AndroidInjector.Factory<WulkanowyApp>
}
