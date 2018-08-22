package io.github.wulkanowy.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import io.github.wulkanowy.data.db.dao.StudentDao
import io.github.wulkanowy.data.db.entities.Student
import javax.inject.Singleton

@Singleton
@Database(
        entities = [Student::class],
        version = 1,
        exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao
}