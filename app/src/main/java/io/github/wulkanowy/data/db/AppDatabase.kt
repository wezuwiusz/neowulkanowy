package io.github.wulkanowy.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import io.github.wulkanowy.data.db.dao.*
import io.github.wulkanowy.data.db.entities.*
import javax.inject.Singleton

@Singleton
@Database(
        entities = [
            Student::class,
            Semester::class,
            Exam::class,
            Timetable::class,
            Attendance::class,
            Grade::class,
            GradeSummary::class
        ],
        version = 1,
        exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        fun newInstance(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "wulkanowy_database")
                    .build()
        }
    }

    abstract fun studentDao(): StudentDao

    abstract fun semesterDao(): SemesterDao

    abstract fun examsDao(): ExamDao

    abstract fun timetableDao(): TimetableDao

    abstract fun attendanceDao(): AttendanceDao

    abstract fun gradeDao(): GradeDao

    abstract fun gradeSummaryDao(): GradeSummaryDao
}
