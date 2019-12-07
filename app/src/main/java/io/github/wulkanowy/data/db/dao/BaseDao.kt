package io.github.wulkanowy.data.db.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

interface BaseDao<T> {

    @Insert
    fun insertAll(items: List<T>): List<Long>

    @Update
    fun updateAll(items: List<T>)

    @Delete
    fun deleteAll(items: List<T>)
}
