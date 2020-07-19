package io.github.wulkanowy.data.repositories.homework

import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.networkBoundResource
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

    fun getHomework(student: Student, semester: Semester, start: LocalDate, end: LocalDate, forceRefresh: Boolean) = networkBoundResource(
        shouldFetch = { it.isEmpty() || forceRefresh },
        query = { local.getHomework(semester, start.monday, end.sunday) },
        fetch = { remote.getHomework(student, semester, start.monday, end.sunday) },
        saveFetchResult = { old, new ->
            local.deleteHomework(old uniqueSubtract new)
            local.saveHomework(new uniqueSubtract old)
        }
    )

    suspend fun toggleDone(homework: Homework) {
        local.updateHomework(listOf(homework.apply {
            isDone = !isDone
        }))
    }
}
