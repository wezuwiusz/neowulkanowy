package io.github.wulkanowy.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import io.github.wulkanowy.data.db.entities.Semester

@Dao
interface SemesterDao {

    @Insert(onConflict = REPLACE)
    fun insert(semester: Semester): Long
}
