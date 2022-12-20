package io.github.wulkanowy.data.db

import android.content.Context
import androidx.room.*
import androidx.room.RoomDatabase.JournalMode.TRUNCATE
import io.github.wulkanowy.data.db.dao.*
import io.github.wulkanowy.data.db.entities.*
import io.github.wulkanowy.data.db.migrations.*
import io.github.wulkanowy.utils.AppInfo
import javax.inject.Singleton

@Singleton
@Database(
    entities = [
        Student::class,
        Semester::class,
        Exam::class,
        Timetable::class,
        Attendance::class,
        AttendanceSummary::class,
        Grade::class,
        GradeSummary::class,
        GradePartialStatistics::class,
        GradePointsStatistics::class,
        GradeSemesterStatistics::class,
        Message::class,
        MessageAttachment::class,
        Note::class,
        Homework::class,
        Subject::class,
        LuckyNumber::class,
        CompletedLesson::class,
        Mailbox::class,
        Recipient::class,
        MobileDevice::class,
        Teacher::class,
        School::class,
        Conference::class,
        TimetableAdditional::class,
        StudentInfo::class,
        TimetableHeader::class,
        SchoolAnnouncement::class,
        Notification::class,
        AdminMessage::class
    ],
    autoMigrations = [
        AutoMigration(from = 44, to = 45),
        AutoMigration(from = 46, to = 47),
        AutoMigration(from = 47, to = 48),
        AutoMigration(from = 51, to = 52),
    ],
    version = AppDatabase.VERSION_SCHEMA,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        const val VERSION_SCHEMA = 54

        fun getMigrations(sharedPrefProvider: SharedPrefProvider, appInfo: AppInfo) = arrayOf(
            Migration2(),
            Migration3(),
            Migration4(),
            Migration5(),
            Migration6(),
            Migration7(),
            Migration8(),
            Migration9(),
            Migration10(),
            Migration11(),
            Migration12(),
            Migration13(),
            Migration14(),
            Migration15(),
            Migration16(),
            Migration17(),
            Migration18(),
            Migration19(sharedPrefProvider),
            Migration20(),
            Migration21(),
            Migration22(),
            Migration23(),
            Migration24(),
            Migration25(),
            Migration26(),
            Migration27(),
            Migration28(),
            Migration29(),
            Migration30(),
            Migration31(),
            Migration32(),
            Migration33(),
            Migration34(),
            Migration35(appInfo),
            Migration36(),
            Migration37(),
            Migration38(),
            Migration39(),
            Migration40(),
            Migration41(sharedPrefProvider),
            Migration42(),
            Migration43(),
            Migration44(),
            Migration46(),
            Migration49(),
            Migration50(),
            Migration51(),
            Migration53(),
            Migration54(),
        )

        fun newInstance(
            context: Context,
            sharedPrefProvider: SharedPrefProvider,
            appInfo: AppInfo
        ) = Room.databaseBuilder(context, AppDatabase::class.java, "wulkanowy_database")
            .setJournalMode(TRUNCATE)
            .fallbackToDestructiveMigrationFrom(VERSION_SCHEMA + 1)
            .fallbackToDestructiveMigrationOnDowngrade()
            .addMigrations(*getMigrations(sharedPrefProvider, appInfo))
            .build()
    }

    abstract val studentDao: StudentDao

    abstract val semesterDao: SemesterDao

    abstract val examsDao: ExamDao

    abstract val timetableDao: TimetableDao

    abstract val attendanceDao: AttendanceDao

    abstract val attendanceSummaryDao: AttendanceSummaryDao

    abstract val gradeDao: GradeDao

    abstract val gradeSummaryDao: GradeSummaryDao

    abstract val gradePartialStatisticsDao: GradePartialStatisticsDao

    abstract val gradePointsStatisticsDao: GradePointsStatisticsDao

    abstract val gradeSemesterStatisticsDao: GradeSemesterStatisticsDao

    abstract val messagesDao: MessagesDao

    abstract val messageAttachmentDao: MessageAttachmentDao

    abstract val noteDao: NoteDao

    abstract val homeworkDao: HomeworkDao

    abstract val subjectDao: SubjectDao

    abstract val luckyNumberDao: LuckyNumberDao

    abstract val completedLessonsDao: CompletedLessonsDao

    abstract val mailboxDao: MailboxDao

    abstract val recipientDao: RecipientDao

    abstract val mobileDeviceDao: MobileDeviceDao

    abstract val teacherDao: TeacherDao

    abstract val schoolDao: SchoolDao

    abstract val conferenceDao: ConferenceDao

    abstract val timetableAdditionalDao: TimetableAdditionalDao

    abstract val studentInfoDao: StudentInfoDao

    abstract val timetableHeaderDao: TimetableHeaderDao

    abstract val schoolAnnouncementDao: SchoolAnnouncementDao

    abstract val notificationDao: NotificationDao

    abstract val adminMessagesDao: AdminMessageDao
}
