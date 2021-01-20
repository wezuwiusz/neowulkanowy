package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.SubjectDao
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.init
import io.github.wulkanowy.utils.networkBoundResource
import io.github.wulkanowy.utils.uniqueSubtract
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectRepository @Inject constructor(
    private val subjectDao: SubjectDao,
    private val sdk: Sdk
) {

    fun getSubjects(student: Student, semester: Semester, forceRefresh: Boolean = false) = networkBoundResource(
        shouldFetch = { it.isEmpty() || forceRefresh },
        query = { subjectDao.loadAll(semester.diaryId, semester.studentId) },
        fetch = {
            sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
                .getSubjects().mapToEntities(semester)
        },
        saveFetchResult = { old, new ->
            subjectDao.deleteAll(old uniqueSubtract new)
            subjectDao.insertAll(new uniqueSubtract old)
        }
    )
}
