package io.github.wulkanowy.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import io.github.wulkanowy.data.db.dao.ExamDao
import io.github.wulkanowy.data.db.dao.SemesterDao
import io.github.wulkanowy.data.db.dao.StudentDao
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import javax.inject.Singleton

@Singleton
@Database(
        entities = [
            Student::class,
            Semester::class,
            Exam::class
        ],
        version = 1,
        exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao

    abstract fun semesterDao(): SemesterDao

    abstract fun examsDao(): ExamDao
}
