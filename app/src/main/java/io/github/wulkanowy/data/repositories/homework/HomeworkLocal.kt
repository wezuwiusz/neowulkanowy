package io.github.wulkanowy.data.repositories.homework

import io.github.wulkanowy.data.db.dao.HomeworkDao
import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Maybe
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeworkLocal @Inject constructor(private val homeworkDb: HomeworkDao) {

    fun getHomework(semester: Semester, date: LocalDate): Maybe<List<Homework>> {
        return homeworkDb.loadAll(semester.semesterId, semester.studentId, date).filter { !it.isEmpty() }
    }

    fun saveHomework(homework: List<Homework>) {
        homeworkDb.insertAll(homework)
    }

    fun deleteHomework(homework: List<Homework>) {
        homeworkDb.deleteAll(homework)
    }
}
