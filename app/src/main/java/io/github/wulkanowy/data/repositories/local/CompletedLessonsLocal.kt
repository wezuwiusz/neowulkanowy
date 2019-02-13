package io.github.wulkanowy.data.repositories.local

import io.github.wulkanowy.data.db.dao.CompletedLessonsDao
import io.github.wulkanowy.data.db.entities.CompletedLesson
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Maybe
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompletedLessonsLocal @Inject constructor(private val completedLessonsDb: CompletedLessonsDao) {

    fun getCompletedLessons(semester: Semester, start: LocalDate, end: LocalDate): Maybe<List<CompletedLesson>> {
        return completedLessonsDb.loadAll(semester.diaryId, semester.studentId, start, end).filter { !it.isEmpty() }
    }

    fun saveCompletedLessons(completedLessons: List<CompletedLesson>) {
        completedLessonsDb.insertAll(completedLessons)
    }

    fun deleteCompleteLessons(completedLessons: List<CompletedLesson>) {
        completedLessonsDb.deleteAll(completedLessons)
    }
}
