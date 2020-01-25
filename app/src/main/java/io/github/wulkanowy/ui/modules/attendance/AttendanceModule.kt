package io.github.wulkanowy.ui.modules.attendance

import dagger.Module
import dagger.Provides
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem

@Module
class AttendanceModule {

    @Provides
    fun provideAttendanceFlexibleAdapter() = AttendanceAdapter<AbstractFlexibleItem<*>>()
}
