package io.github.wulkanowy.data.repositories.conference

import io.github.wulkanowy.data.db.dao.ConferenceDao
import io.github.wulkanowy.data.db.entities.Conference
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConferenceLocal @Inject constructor(private val conferenceDb: ConferenceDao) {

    fun getConferences(student: Student, semester: Semester): Flow<List<Conference>> {
        return conferenceDb.loadAll(semester.diaryId, student.studentId)
    }

    suspend fun saveConferences(items: List<Conference>) {
        conferenceDb.insertAll(items)
    }

    suspend fun deleteConferences(items: List<Conference>) {
        conferenceDb.deleteAll(items)
    }
}
