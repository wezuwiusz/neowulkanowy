package io.github.wulkanowy.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomDatabase.JournalMode.TRUNCATE
import androidx.room.TypeConverters
import io.github.wulkanowy.data.db.dao.AttendanceDao
import io.github.wulkanowy.data.db.dao.AttendanceSummaryDao
import io.github.wulkanowy.data.db.dao.CompletedLessonsDao
import io.github.wulkanowy.data.db.dao.ConferenceDao
import io.github.wulkanowy.data.db.dao.SchoolAnnouncementDao
import io.github.wulkanowy.data.db.dao.ExamDao
import io.github.wulkanowy.data.db.dao.GradeDao
import io.github.wulkanowy.data.db.dao.GradePartialStatisticsDao
import io.github.wulkanowy.data.db.dao.GradePointsStatisticsDao
import io.github.wulkanowy.data.db.dao.GradeSemesterStatisticsDao
import io.github.wulkanowy.data.db.dao.GradeSummaryDao
import io.github.wulkanowy.data.db.dao.HomeworkDao
import io.github.wulkanowy.data.db.dao.LuckyNumberDao
import io.github.wulkanowy.data.db.dao.MessageAttachmentDao
import io.github.wulkanowy.data.db.dao.MessagesDao
import io.github.wulkanowy.data.db.dao.MobileDeviceDao
import io.github.wulkanowy.data.db.dao.NoteDao
import io.github.wulkanowy.data.db.dao.RecipientDao
import io.github.wulkanowy.data.db.dao.ReportingUnitDao
import io.github.wulkanowy.data.db.dao.SchoolDao
import io.github.wulkanowy.data.db.dao.SemesterDao
import io.github.wulkanowy.data.db.dao.StudentDao
import io.github.wulkanowy.data.db.dao.StudentInfoDao
import io.github.wulkanowy.data.db.dao.SubjectDao
import io.github.wulkanowy.data.db.dao.TeacherDao
import io.github.wulkanowy.data.db.dao.TimetableAdditionalDao
import io.github.wulkanowy.data.db.dao.TimetableDao
import io.github.wulkanowy.data.db.dao.TimetableHeaderDao
import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.data.db.entities.AttendanceSummary
import io.github.wulkanowy.data.db.entities.CompletedLesson
import io.github.wulkanowy.data.db.entities.Conference
import io.github.wulkanowy.data.db.entities.SchoolAnnouncement
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.GradePartialStatistics
import io.github.wulkanowy.data.db.entities.GradePointsStatistics
import io.github.wulkanowy.data.db.entities.GradeSemesterStatistics
import io.github.wulkanowy.data.db.entities.GradeSummary
import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.MessageAttachment
import io.github.wulkanowy.data.db.entities.MobileDevice
import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.github.wulkanowy.data.db.entities.School
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.StudentInfo
import io.github.wulkanowy.data.db.entities.Subject
import io.github.wulkanowy.data.db.entities.Teacher
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.data.db.entities.TimetableAdditional
import io.github.wulkanowy.data.db.entities.TimetableHeader
import io.github.wulkanowy.data.db.migrations.Migration10
import io.github.wulkanowy.data.db.migrations.Migration11
import io.github.wulkanowy.data.db.migrations.Migration12
import io.github.wulkanowy.data.db.migrations.Migration13
import io.github.wulkanowy.data.db.migrations.Migration14
import io.github.wulkanowy.data.db.migrations.Migration15
import io.github.wulkanowy.data.db.migrations.Migration16
import io.github.wulkanowy.data.db.migrations.Migration17
import io.github.wulkanowy.data.db.migrations.Migration18
import io.github.wulkanowy.data.db.migrations.Migration19
import io.github.wulkanowy.data.db.migrations.Migration2
import io.github.wulkanowy.data.db.migrations.Migration20
import io.github.wulkanowy.data.db.migrations.Migration21
import io.github.wulkanowy.data.db.migrations.Migration22
import io.github.wulkanowy.data.db.migrations.Migration23
import io.github.wulkanowy.data.db.migrations.Migration24
import io.github.wulkanowy.data.db.migrations.Migration25
import io.github.wulkanowy.data.db.migrations.Migration26
import io.github.wulkanowy.data.db.migrations.Migration27
import io.github.wulkanowy.data.db.migrations.Migration28
import io.github.wulkanowy.data.db.migrations.Migration29
import io.github.wulkanowy.data.db.migrations.Migration3
import io.github.wulkanowy.data.db.migrations.Migration30
import io.github.wulkanowy.data.db.migrations.Migration31
import io.github.wulkanowy.data.db.migrations.Migration32
import io.github.wulkanowy.data.db.migrations.Migration33
import io.github.wulkanowy.data.db.migrations.Migration34
import io.github.wulkanowy.data.db.migrations.Migration35
import io.github.wulkanowy.data.db.migrations.Migration36
import io.github.wulkanowy.data.db.migrations.Migration37
import io.github.wulkanowy.data.db.migrations.Migration38
import io.github.wulkanowy.data.db.migrations.Migration39
import io.github.wulkanowy.data.db.migrations.Migration4
import io.github.wulkanowy.data.db.migrations.Migration5
import io.github.wulkanowy.data.db.migrations.Migration6
import io.github.wulkanowy.data.db.migrations.Migration7
import io.github.wulkanowy.data.db.migrations.Migration8
import io.github.wulkanowy.data.db.migrations.Migration9
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
        ReportingUnit::class,
        Recipient::class,
        MobileDevice::class,
        Teacher::class,
        School::class,
        Conference::class,
        TimetableAdditional::class,
        StudentInfo::class,
        TimetableHeader::class,
        SchoolAnnouncement::class,
    ],
    version = AppDatabase.VERSION_SCHEMA,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        const val VERSION_SCHEMA = 39

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

    abstract val reportingUnitDao: ReportingUnitDao

    abstract val recipientDao: RecipientDao

    abstract val mobileDeviceDao: MobileDeviceDao

    abstract val teacherDao: TeacherDao

    abstract val schoolDao: SchoolDao

    abstract val conferenceDao: ConferenceDao

    abstract val timetableAdditionalDao: TimetableAdditionalDao

    abstract val studentInfoDao: StudentInfoDao

    abstract val timetableHeaderDao: TimetableHeaderDao

    abstract val schoolAnnouncementDao: SchoolAnnouncementDao
}
