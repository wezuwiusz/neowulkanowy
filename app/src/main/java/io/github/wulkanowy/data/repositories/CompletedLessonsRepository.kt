package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.CompletedLessonsDao
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.init
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.networkBoundResource
import io.github.wulkanowy.utils.sunday
import io.github.wulkanowy.utils.uniqueSubtract
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompletedLessonsRepository @Inject constructor(
    private val completedLessonsDb: CompletedLessonsDao,
    private val sdk: Sdk
) {

    fun getCompletedLessons(student: Student, semester: Semester, start: LocalDate, end: LocalDate, forceRefresh: Boolean) = networkBoundResource(
        shouldFetch = { it.isEmpty() || forceRefresh },
        query = { completedLessonsDb.loadAll(semester.diaryId, semester.studentId, start, end) },
        fetch = {
            sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
                .getCompletedLessons(start.monday, end.sunday)
                .mapToEntities(semester)
        },
        saveFetchResult = { old, new ->
            completedLessonsDb.deleteAll(old uniqueSubtract new)
            completedLessonsDb.insertAll(new uniqueSubtract old)
        },
        filterResult = { it.filter { item -> item.date in start..end } }
    )
}
