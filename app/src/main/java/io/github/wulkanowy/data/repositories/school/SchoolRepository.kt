package io.github.wulkanowy.data.repositories.school

import io.github.wulkanowy.data.db.entities.School
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SchoolRepository @Inject constructor(
    private val local: SchoolLocal,
    private val remote: SchoolRemote
) {

    suspend fun getSchoolInfo(student: Student, semester: Semester, forceRefresh: Boolean = false): School {
        return local.getSchool(semester).takeIf { it != null && !forceRefresh } ?: run {
            val new = remote.getSchoolInfo(student, semester)
            val old = local.getSchool(semester)

            if (new != old && old != null) {
                local.deleteSchool(old)
                local.saveSchool(new)
            }
            local.saveSchool(new)

            local.getSchool(semester)!!
        }
    }
}
