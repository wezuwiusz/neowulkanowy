package io.github.wulkanowy.data.repositories.homework

import io.github.wulkanowy.data.db.dao.HomeworkDao
import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.data.db.entities.Semester
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeworkLocal @Inject constructor(private val homeworkDb: HomeworkDao) {

    suspend fun saveHomework(homework: List<Homework>) {
        homeworkDb.insertAll(homework)
    }

    suspend fun deleteHomework(homework: List<Homework>) {
        homeworkDb.deleteAll(homework)
    }

    suspend fun updateHomework(homework: List<Homework>) {
        homeworkDb.updateAll(homework)
    }

    fun getHomework(semester: Semester, startDate: LocalDate, endDate: LocalDate): Flow<List<Homework>> {
        return homeworkDb.loadAll(semester.semesterId, semester.studentId, startDate, endDate)
    }
}
