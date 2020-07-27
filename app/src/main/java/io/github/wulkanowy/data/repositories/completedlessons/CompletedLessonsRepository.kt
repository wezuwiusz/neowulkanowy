package io.github.wulkanowy.data.repositories.completedlessons

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.networkBoundResource
import io.github.wulkanowy.utils.sunday
import io.github.wulkanowy.utils.uniqueSubtract
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompletedLessonsRepository @Inject constructor(
    private val local: CompletedLessonsLocal,
    private val remote: CompletedLessonsRemote
) {

    fun getCompletedLessons(student: Student, semester: Semester, start: LocalDate, end: LocalDate, forceRefresh: Boolean) = networkBoundResource(
        shouldFetch = { it.isEmpty() || forceRefresh },
        query = { local.getCompletedLessons(semester, start.monday, end.sunday) },
        fetch = { remote.getCompletedLessons(student, semester, start.monday, end.sunday) },
        saveFetchResult = { old, new ->
            local.deleteCompleteLessons(old uniqueSubtract new)
            local.saveCompletedLessons(new uniqueSubtract old)
        },
        filterResult = { it.filter { item -> item.date in start..end } }
    )
}
