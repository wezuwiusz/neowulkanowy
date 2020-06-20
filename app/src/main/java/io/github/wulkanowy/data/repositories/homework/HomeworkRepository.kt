package io.github.wulkanowy.data.repositories.homework

import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.sunday
import io.github.wulkanowy.utils.uniqueSubtract
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeworkRepository @Inject constructor(
    private val local: HomeworkLocal,
    private val remote: HomeworkRemote
) {

    suspend fun getHomework(student: Student, semester: Semester, start: LocalDate, end: LocalDate, forceRefresh: Boolean = false): List<Homework> {
        return local.getHomework(semester, start.monday, end.sunday).filter { !forceRefresh }.ifEmpty {
            val new = remote.getHomework(student, semester, start.monday, end.sunday)

            val old = local.getHomework(semester, start.monday, end.sunday)

            local.deleteHomework(old.uniqueSubtract(new))
            local.saveHomework(new.uniqueSubtract(old))

            local.getHomework(semester, start.monday, end.sunday)
        }
    }

    suspend fun toggleDone(homework: Homework) {
        local.updateHomework(listOf(homework.apply {
            isDone = !isDone
        }))
    }
}
