package io.github.wulkanowy.data.repositories.school

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.networkBoundResource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SchoolRepository @Inject constructor(
    private val local: SchoolLocal,
    private val remote: SchoolRemote
) {

    fun getSchoolInfo(student: Student, semester: Semester, forceRefresh: Boolean) = networkBoundResource(
        shouldFetch = { it == null || forceRefresh },
        query = { local.getSchool(semester) },
        fetch = { remote.getSchoolInfo(student, semester) },
        saveFetchResult = { old, new ->
            if (new != old && old != null) {
                local.deleteSchool(old)
                local.saveSchool(new)
            }
            local.saveSchool(new)
        }
    )
}
