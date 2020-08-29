package io.github.wulkanowy.data.repositories.teacher

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.networkBoundResource
import io.github.wulkanowy.utils.uniqueSubtract
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeacherRepository @Inject constructor(
    private val local: TeacherLocal,
    private val remote: TeacherRemote
) {

    fun getTeachers(student: Student, semester: Semester, forceRefresh: Boolean) = networkBoundResource(
        shouldFetch = { it.isEmpty() || forceRefresh },
        query = { local.getTeachers(semester) },
        fetch = { remote.getTeachers(student, semester) },
        saveFetchResult = { old, new ->
            local.deleteTeachers(old uniqueSubtract new)
            local.saveTeachers(new uniqueSubtract old)
        }
    )
}
