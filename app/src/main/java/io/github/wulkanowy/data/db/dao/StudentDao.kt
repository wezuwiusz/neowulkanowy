package io.github.wulkanowy.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import io.github.wulkanowy.data.db.entities.Student
import io.reactivex.Maybe

@Dao
interface StudentDao {

    @Insert(onConflict = REPLACE)
    fun insert(student: Student): Long

    @Query("SELECT * FROM Students WHERE id = :id")
    fun load(id: Long): Maybe<Student>
}
