package io.github.wulkanowy.data.repositories.subject

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.networkBoundResource
import io.github.wulkanowy.utils.uniqueSubtract
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectRepository @Inject constructor(
    private val local: SubjectLocal,
    private val remote: SubjectRemote
) {

    fun getSubjects(student: Student, semester: Semester, forceRefresh: Boolean = false) = networkBoundResource(
        shouldFetch = { it.isEmpty() || forceRefresh },
        query = { local.getSubjects(semester) },
        fetch = { remote.getSubjects(student, semester) },
        saveFetchResult = { old, new ->
            local.deleteSubjects(old uniqueSubtract new)
            local.saveSubjects(new uniqueSubtract old)
        }
    )
}
