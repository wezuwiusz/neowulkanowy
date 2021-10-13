package io.github.wulkanowy.ui.modules.dashboard

import io.github.wulkanowy.data.db.entities.AdminMessage
import io.github.wulkanowy.data.db.entities.Conference
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.SchoolAnnouncement
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.TimetableFull
import io.github.wulkanowy.data.db.entities.Homework as EntitiesHomework

sealed class DashboardItem(val type: Type) {

    abstract val error: Throwable?

    abstract val isLoading: Boolean

    abstract val isDataLoaded: Boolean

    data class AdminMessages(
        val adminMessage: AdminMessage? = null,
        override val error: Throwable? = null,
        override val isLoading: Boolean = false
    ) : DashboardItem(Type.ADMIN_MESSAGE) {

        override val isDataLoaded get() = adminMessage != null
    }

    data class Account(
        val student: Student? = null,
        override val error: Throwable? = null,
        override val isLoading: Boolean = false
    ) : DashboardItem(Type.ACCOUNT) {

        override val isDataLoaded get() = student != null
    }

    data class HorizontalGroup(
        val unreadMessagesCount: Int? = null,
        val attendancePercentage: Double? = null,
        val luckyNumber: Int? = null,
        override val error: Throwable? = null,
        override val isLoading: Boolean = false
    ) : DashboardItem(Type.HORIZONTAL_GROUP) {

        override val isDataLoaded
            get() = unreadMessagesCount != null || attendancePercentage != null || luckyNumber != null

        val isFullDataLoaded
            get() = luckyNumber != -1 && attendancePercentage != -1.0 && unreadMessagesCount != -1
    }

    data class Grades(
        val subjectWithGrades: Map<String, List<Grade>>? = null,
        val gradeTheme: String? = null,
        override val error: Throwable? = null,
        override val isLoading: Boolean = false
    ) : DashboardItem(Type.GRADES) {

        override val isDataLoaded get() = subjectWithGrades != null
    }

    data class Lessons(
        val lessons: TimetableFull? = null,
        override val error: Throwable? = null,
        override val isLoading: Boolean = false
    ) : DashboardItem(Type.LESSONS) {

        override val isDataLoaded get() = lessons != null
    }

    data class Homework(
        val homework: List<EntitiesHomework>? = null,
        override val error: Throwable? = null,
        override val isLoading: Boolean = false
    ) : DashboardItem(Type.HOMEWORK) {

        override val isDataLoaded get() = homework != null
    }

    data class Announcements(
        val announcement: List<SchoolAnnouncement>? = null,
        override val error: Throwable? = null,
        override val isLoading: Boolean = false
    ) : DashboardItem(Type.ANNOUNCEMENTS) {

        override val isDataLoaded get() = announcement != null
    }

    data class Exams(
        val exams: List<Exam>? = null,
        override val error: Throwable? = null,
        override val isLoading: Boolean = false
    ) : DashboardItem(Type.EXAMS) {

        override val isDataLoaded get() = exams != null
    }

    data class Conferences(
        val conferences: List<Conference>? = null,
        override val error: Throwable? = null,
        override val isLoading: Boolean = false
    ) : DashboardItem(Type.CONFERENCES) {

        override val isDataLoaded get() = conferences != null
    }

    enum class Type {
        ADMIN_MESSAGE,
        ACCOUNT,
        HORIZONTAL_GROUP,
        LESSONS,
        GRADES,
        HOMEWORK,
        ANNOUNCEMENTS,
        EXAMS,
        CONFERENCES,
        ADS
    }

    enum class Tile {
        ADMIN_MESSAGE,
        ACCOUNT,
        LUCKY_NUMBER,
        MESSAGES,
        ATTENDANCE,
        LESSONS,
        GRADES,
        HOMEWORK,
        ANNOUNCEMENTS,
        EXAMS,
        CONFERENCES,
        ADS
    }
}

fun DashboardItem.Tile.toDashboardItemType() = when (this) {
    DashboardItem.Tile.ADMIN_MESSAGE -> DashboardItem.Type.ADMIN_MESSAGE
    DashboardItem.Tile.ACCOUNT -> DashboardItem.Type.ACCOUNT
    DashboardItem.Tile.LUCKY_NUMBER -> DashboardItem.Type.HORIZONTAL_GROUP
    DashboardItem.Tile.MESSAGES -> DashboardItem.Type.HORIZONTAL_GROUP
    DashboardItem.Tile.ATTENDANCE -> DashboardItem.Type.HORIZONTAL_GROUP
    DashboardItem.Tile.LESSONS -> DashboardItem.Type.LESSONS
    DashboardItem.Tile.GRADES -> DashboardItem.Type.GRADES
    DashboardItem.Tile.HOMEWORK -> DashboardItem.Type.HOMEWORK
    DashboardItem.Tile.ANNOUNCEMENTS -> DashboardItem.Type.ANNOUNCEMENTS
    DashboardItem.Tile.EXAMS -> DashboardItem.Type.EXAMS
    DashboardItem.Tile.CONFERENCES -> DashboardItem.Type.CONFERENCES
    DashboardItem.Tile.ADS -> DashboardItem.Type.ADS
}