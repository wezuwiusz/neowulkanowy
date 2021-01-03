package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.ExamDao
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.endExamsDay
import io.github.wulkanowy.utils.init
import io.github.wulkanowy.utils.networkBoundResource
import io.github.wulkanowy.utils.startExamsDay
import io.github.wulkanowy.utils.uniqueSubtract
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExamRepository @Inject constructor(
    private val examDb: ExamDao,
    private val sdk: Sdk
) {

    fun getExams(student: Student, semester: Semester, start: LocalDate, end: LocalDate, forceRefresh: Boolean) = networkBoundResource(
        shouldFetch = { it.isEmpty() || forceRefresh },
        query = { examDb.loadAll(semester.diaryId, semester.studentId, start.startExamsDay, start.endExamsDay) },
        fetch = {
            sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
                .getExams(start.startExamsDay, start.endExamsDay, semester.semesterId)
                .mapToEntities(semester)
        },
        saveFetchResult = { old, new ->
            examDb.deleteAll(old uniqueSubtract new)
            examDb.insertAll(new uniqueSubtract old)
        },
        filterResult = { it.filter { item -> item.date in start..end } }
    )
}
