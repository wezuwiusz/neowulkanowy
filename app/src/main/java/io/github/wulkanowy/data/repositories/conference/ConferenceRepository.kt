package io.github.wulkanowy.data.repositories.conference

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.networkBoundResource
import io.github.wulkanowy.utils.uniqueSubtract
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConferenceRepository @Inject constructor(
    private val local: ConferenceLocal,
    private val remote: ConferenceRemote
) {

    fun getConferences(student: Student, semester: Semester, forceRefresh: Boolean) = networkBoundResource(
        shouldFetch = { it.isEmpty() || forceRefresh },
        query = { local.getConferences(student, semester) },
        fetch = { remote.getConferences(student, semester) },
        saveFetchResult = { old, new ->
            local.deleteConferences(old uniqueSubtract new)
            local.saveConferences(new uniqueSubtract old)
        }
    )
}
