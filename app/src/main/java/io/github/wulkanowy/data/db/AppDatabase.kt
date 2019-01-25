package io.github.wulkanowy.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomDatabase.JournalMode.TRUNCATE
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.wulkanowy.data.db.dao.AttendanceDao
import io.github.wulkanowy.data.db.dao.AttendanceSummaryDao
import io.github.wulkanowy.data.db.dao.ExamDao
import io.github.wulkanowy.data.db.dao.GradeDao
import io.github.wulkanowy.data.db.dao.GradeSummaryDao
import io.github.wulkanowy.data.db.dao.MessagesDao
import io.github.wulkanowy.data.db.dao.HomeworkDao
import io.github.wulkanowy.data.db.dao.LuckyNumberDao
import io.github.wulkanowy.data.db.dao.NoteDao
import io.github.wulkanowy.data.db.dao.SemesterDao
import io.github.wulkanowy.data.db.dao.StudentDao
import io.github.wulkanowy.data.db.dao.SubjectDao
import io.github.wulkanowy.data.db.dao.TimetableDao
import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.data.db.entities.AttendanceSummary
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.GradeSummary
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.Subject
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.data.db.migrations.Migration2
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
        Message::class,
        Note::class,
        Homework::class,
        Subject::class,
        LuckyNumber::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        fun newInstance(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "wulkanowy_database")
                .setJournalMode(TRUNCATE)
                .addMigrations(
                    Migration2()
                )
                .build()
        }
    }

    abstract val studentDao: StudentDao

    abstract val semesterDao: SemesterDao

    abstract val examsDao: ExamDao

    abstract val timetableDao: TimetableDao

    abstract val attendanceDao: AttendanceDao

    abstract val attendanceSummaryDao: AttendanceSummaryDao

    abstract val gradeDao: GradeDao

    abstract val gradeSummaryDao: GradeSummaryDao

    abstract val messagesDao: MessagesDao

    abstract val noteDao: NoteDao

    abstract val homeworkDao: HomeworkDao

    abstract val subjectDao: SubjectDao

    abstract val luckyNumberDao: LuckyNumberDao
}
