package io.github.wulkanowy.data.repositories.completedlessons

import io.github.wulkanowy.data.db.entities.CompletedLesson
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.sunday
import io.github.wulkanowy.utils.uniqueSubtract
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompletedLessonsRepository @Inject constructor(
    private val local: CompletedLessonsLocal,
    private val remote: CompletedLessonsRemote
) {

    suspend fun getCompletedLessons(student: Student, semester: Semester, start: LocalDate, end: LocalDate, forceRefresh: Boolean = false): List<CompletedLesson> {
        return local.getCompletedLessons(semester, start.monday, end.sunday).filter { !forceRefresh }.ifEmpty {
            val new = remote.getCompletedLessons(student, semester, start.monday, end.sunday)
            val old = local.getCompletedLessons(semester, start.monday, end.sunday)

            local.deleteCompleteLessons(old.uniqueSubtract(new))
            local.saveCompletedLessons(new.uniqueSubtract(old))

            local.getCompletedLessons(semester, start.monday, end.sunday)
        }.filter { it.date in start..end }
    }
}
