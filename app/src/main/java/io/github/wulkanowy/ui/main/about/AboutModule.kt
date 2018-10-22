package io.github.wulkanowy.ui.main.about

import com.mikepenz.aboutlibraries.LibsFragmentCompat
import dagger.Module
import dagger.Provides
import io.github.wulkanowy.di.scopes.PerFragment

@Module
class AboutModule {

    @PerFragment
    @Provides
    fun provideLibsFragmentCompat() = LibsFragmentCompat()
}

