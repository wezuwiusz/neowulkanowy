package io.github.wulkanowy.ui.modules.mobiledevice

import dagger.Module
import dagger.Provides
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem

@Module
class MobileDeviceModule {

    @Provides
    fun provideMobileDeviceFlexibleAdapter() = MobileDeviceAdapter<AbstractFlexibleItem<*>>()
}
