package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.SchoolDao
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.mappers.mapToEntity
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.init
import io.github.wulkanowy.utils.networkBoundResource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SchoolRepository @Inject constructor(
    private val schoolDb: SchoolDao,
    private val sdk: Sdk
) {

    fun getSchoolInfo(student: Student, semester: Semester, forceRefresh: Boolean) =
        networkBoundResource(
            shouldFetch = { it == null || forceRefresh },
            query = { schoolDb.load(semester.studentId, semester.classId) },
            fetch = {
                sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear).getSchool()
                    .mapToEntity(semester)
            },
            saveFetchResult = { old, new ->
                if (old != null && new != old) {
                    with(schoolDb) {
                        deleteAll(listOf(old))
                        insertAll(listOf(new))
                    }
                } else if (old == null) {
                    schoolDb.insertAll(listOf(new))
                }
            }
        )
}
