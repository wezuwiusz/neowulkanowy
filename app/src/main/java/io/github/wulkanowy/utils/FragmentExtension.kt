package io.github.wulkanowy.utils

import androidx.fragment.app.Fragment
import io.github.wulkanowy.ui.modules.about.AboutFragment
import io.github.wulkanowy.ui.modules.account.AccountFragment
import io.github.wulkanowy.ui.modules.account.accountdetails.AccountDetailsFragment
import io.github.wulkanowy.ui.modules.attendance.AttendanceFragment
import io.github.wulkanowy.ui.modules.conference.ConferenceFragment
import io.github.wulkanowy.ui.modules.exam.ExamFragment
import io.github.wulkanowy.ui.modules.grade.GradeFragment
import io.github.wulkanowy.ui.modules.homework.HomeworkFragment
import io.github.wulkanowy.ui.modules.luckynumber.LuckyNumberFragment
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.message.MessageFragment
import io.github.wulkanowy.ui.modules.more.MoreFragment
import io.github.wulkanowy.ui.modules.note.NoteFragment
import io.github.wulkanowy.ui.modules.schoolandteachers.SchoolAndTeachersFragment
import io.github.wulkanowy.ui.modules.schoolannouncement.SchoolAnnouncementFragment
import io.github.wulkanowy.ui.modules.settings.SettingsFragment
import io.github.wulkanowy.ui.modules.studentinfo.StudentInfoFragment
import io.github.wulkanowy.ui.modules.timetable.TimetableFragment

fun Fragment.toSection(): MainView.Section? {
    return when (this) {
        is GradeFragment -> MainView.Section.GRADE
        is AttendanceFragment -> MainView.Section.ATTENDANCE
        is ExamFragment -> MainView.Section.EXAM
        is TimetableFragment -> MainView.Section.TIMETABLE
        is MoreFragment -> MainView.Section.MORE
        is MessageFragment -> MainView.Section.MESSAGE
        is HomeworkFragment -> MainView.Section.HOMEWORK
        is NoteFragment -> MainView.Section.NOTE
        is LuckyNumberFragment -> MainView.Section.LUCKY_NUMBER
        is SettingsFragment -> MainView.Section.SETTINGS
        is AboutFragment -> MainView.Section.ABOUT
        is SchoolAndTeachersFragment -> MainView.Section.SCHOOL
        is AccountFragment -> MainView.Section.ACCOUNT
        is AccountDetailsFragment -> MainView.Section.ACCOUNT
        is StudentInfoFragment -> MainView.Section.STUDENT_INFO
        is ConferenceFragment -> MainView.Section.CONFERENCE
        is SchoolAnnouncementFragment -> MainView.Section.SCHOOL_ANNOUNCEMENT
        else -> null
    }
}
