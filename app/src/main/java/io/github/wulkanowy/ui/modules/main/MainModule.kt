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
import io.github.wulkanowy.ui.modules.account.AccountDialog
import io.github.wulkanowy.ui.modules.attendance.AttendanceFragment
import io.github.wulkanowy.ui.modules.attendance.summary.AttendanceSummaryFragment
import io.github.wulkanowy.ui.modules.exam.ExamFragment
import io.github.wulkanowy.ui.modules.grade.GradeFragment
import io.github.wulkanowy.ui.modules.grade.GradeModule
import io.github.wulkanowy.ui.modules.homework.HomeworkFragment
import io.github.wulkanowy.ui.modules.luckynumber.LuckyNumberFragment
import io.github.wulkanowy.ui.modules.message.MessageFragment
import io.github.wulkanowy.ui.modules.message.MessageModule
import io.github.wulkanowy.ui.modules.message.preview.MessagePreviewFragment
import io.github.wulkanowy.ui.modules.more.MoreFragment
import io.github.wulkanowy.ui.modules.note.NoteFragment
import io.github.wulkanowy.ui.modules.settings.SettingsFragment
import io.github.wulkanowy.ui.modules.timetable.TimetableFragment
import io.github.wulkanowy.ui.modules.timetable.completed.CompletedLessonsFragment

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
    @ContributesAndroidInjector(modules = [AboutModule::class])
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
}
