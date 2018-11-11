package io.github.wulkanowy.ui.modules.main

import com.ncapdevi.fragnav.FragNavController
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import io.github.wulkanowy.R
import io.github.wulkanowy.di.scopes.PerActivity
import io.github.wulkanowy.di.scopes.PerFragment
import io.github.wulkanowy.ui.modules.about.AboutFragment
import io.github.wulkanowy.ui.modules.about.AboutModule
import io.github.wulkanowy.ui.modules.attendance.AttendanceFragment
import io.github.wulkanowy.ui.modules.exam.ExamFragment
import io.github.wulkanowy.ui.modules.grade.GradeFragment
import io.github.wulkanowy.ui.modules.grade.GradeModule
import io.github.wulkanowy.ui.modules.more.MoreFragment
import io.github.wulkanowy.ui.modules.note.NoteFragment
import io.github.wulkanowy.ui.modules.settings.SettingsFragment
import io.github.wulkanowy.ui.modules.timetable.TimetableFragment

@Module
abstract class MainModule {

    @Module
    companion object {

        @JvmStatic
        @PerActivity
        @Provides
        fun provideFragNavController(activity: MainActivity): FragNavController {
            return FragNavController(activity.supportFragmentManager, R.id.mainFragmentContainer)
        }
    }

    @PerFragment
    @ContributesAndroidInjector
    abstract fun bindAttendanceFragment(): AttendanceFragment

    @PerFragment
    @ContributesAndroidInjector
    abstract fun bindExamFragment(): ExamFragment

    @PerFragment
    @ContributesAndroidInjector(modules = [GradeModule::class])
    abstract fun bindGradeFragment(): GradeFragment

    @PerFragment
    @ContributesAndroidInjector
    abstract fun bindMoreFragment(): MoreFragment

    @PerFragment
    @ContributesAndroidInjector
    abstract fun bindTimetableFragment(): TimetableFragment

    @PerFragment
    @ContributesAndroidInjector(modules = [AboutModule::class])
    abstract fun bindAboutFragment(): AboutFragment

    @ContributesAndroidInjector
    abstract fun bindSettingsFragment(): SettingsFragment

    @ContributesAndroidInjector
    abstract fun bindNoteFragment(): NoteFragment
}
