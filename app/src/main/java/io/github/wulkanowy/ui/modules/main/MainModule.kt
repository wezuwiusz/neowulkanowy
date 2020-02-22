package io.github.wulkanowy.ui.modules.main

import com.google.android.material.elevation.ElevationOverlayProvider
import com.ncapdevi.fragnav.FragNavController
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import io.github.wulkanowy.R
import io.github.wulkanowy.di.scopes.PerFragment
import io.github.wulkanowy.ui.modules.about.AboutFragment
import io.github.wulkanowy.ui.modules.about.creator.CreatorFragment
import io.github.wulkanowy.ui.modules.about.license.LicenseFragment
import io.github.wulkanowy.ui.modules.about.license.LicenseModule
import io.github.wulkanowy.ui.modules.about.logviewer.LogViewerFragment
import io.github.wulkanowy.ui.modules.account.AccountDialog
import io.github.wulkanowy.ui.modules.attendance.AttendanceFragment
import io.github.wulkanowy.ui.modules.attendance.AttendanceModule
import io.github.wulkanowy.ui.modules.attendance.summary.AttendanceSummaryFragment
import io.github.wulkanowy.ui.modules.exam.ExamFragment
import io.github.wulkanowy.ui.modules.grade.GradeFragment
import io.github.wulkanowy.ui.modules.grade.GradeModule
import io.github.wulkanowy.ui.modules.homework.HomeworkFragment
import io.github.wulkanowy.ui.modules.luckynumber.LuckyNumberFragment
import io.github.wulkanowy.ui.modules.message.MessageFragment
import io.github.wulkanowy.ui.modules.message.MessageModule
import io.github.wulkanowy.ui.modules.message.preview.MessagePreviewFragment
import io.github.wulkanowy.ui.modules.mobiledevice.MobileDeviceFragment
import io.github.wulkanowy.ui.modules.mobiledevice.MobileDeviceModule
import io.github.wulkanowy.ui.modules.mobiledevice.token.MobileDeviceTokenDialog
import io.github.wulkanowy.ui.modules.more.MoreFragment
import io.github.wulkanowy.ui.modules.note.NoteFragment
import io.github.wulkanowy.ui.modules.schoolandteachers.SchoolAndTeachersFragment
import io.github.wulkanowy.ui.modules.schoolandteachers.SchoolAndTeachersModule
import io.github.wulkanowy.ui.modules.settings.SettingsFragment
import io.github.wulkanowy.ui.modules.timetable.TimetableFragment
import io.github.wulkanowy.ui.modules.timetable.completed.CompletedLessonsFragment

@Suppress("unused")
@Module
abstract class MainModule {

    companion object {

        @Provides
        fun provideFragNavController(activity: MainActivity) =
            FragNavController(activity.supportFragmentManager, R.id.mainFragmentContainer)

        //In activities must be injected as Lazy
        @Provides
        fun provideElevationOverlayProvider(activity: MainActivity) = ElevationOverlayProvider(activity)
    }

    @PerFragment
    @ContributesAndroidInjector(modules = [AttendanceModule::class])
    abstract fun bindAttendanceFragment(): AttendanceFragment

    @PerFragment
    @ContributesAndroidInjector
    abstract fun bindAttendanceSummaryFragment(): AttendanceSummaryFragment

    @PerFragment
    @ContributesAndroidInjector
    abstract fun bindExamFragment(): ExamFragment

    @PerFragment
    @ContributesAndroidInjector(modules = [GradeModule::class])
    abstract fun bindGradeFragment(): GradeFragment

    @PerFragment
    @ContributesAndroidInjector(modules = [MessageModule::class])
    abstract fun bindMessagesFragment(): MessageFragment

    @PerFragment
    @ContributesAndroidInjector
    abstract fun bindMessagePreviewFragment(): MessagePreviewFragment

    @PerFragment
    @ContributesAndroidInjector
    abstract fun bindMoreFragment(): MoreFragment

    @PerFragment
    @ContributesAndroidInjector
    abstract fun bindTimetableFragment(): TimetableFragment

    @PerFragment
    @ContributesAndroidInjector
    abstract fun bindAboutFragment(): AboutFragment

    @PerFragment
    @ContributesAndroidInjector
    abstract fun bindSettingsFragment(): SettingsFragment

    @PerFragment
    @ContributesAndroidInjector
    abstract fun bindNoteFragment(): NoteFragment

    @PerFragment
    @ContributesAndroidInjector
    abstract fun bindHomeworkFragment(): HomeworkFragment

    @PerFragment
    @ContributesAndroidInjector
    abstract fun bindLuckyNumberFragment(): LuckyNumberFragment

    @PerFragment
    @ContributesAndroidInjector
    abstract fun bindCompletedLessonsFragment(): CompletedLessonsFragment

    @PerFragment
    @ContributesAndroidInjector
    abstract fun bindAccountDialog(): AccountDialog

    @PerFragment
    @ContributesAndroidInjector(modules = [MobileDeviceModule::class])
    abstract fun bindMobileDevices(): MobileDeviceFragment

    @PerFragment
    @ContributesAndroidInjector
    abstract fun bindMobileDeviceDialog(): MobileDeviceTokenDialog

    @PerFragment
    @ContributesAndroidInjector(modules = [LicenseModule::class])
    abstract fun bindLicenseFragment(): LicenseFragment

    @PerFragment
    @ContributesAndroidInjector
    abstract fun bindLogViewerFragment(): LogViewerFragment

    @PerFragment
    @ContributesAndroidInjector()
    abstract fun bindCreatorsFragment(): CreatorFragment

    @PerFragment
    @ContributesAndroidInjector(modules = [SchoolAndTeachersModule::class])
    abstract fun bindSchoolAndTeachersFragment(): SchoolAndTeachersFragment
}
