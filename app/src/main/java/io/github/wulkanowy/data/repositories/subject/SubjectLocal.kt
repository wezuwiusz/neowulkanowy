package io.github.wulkanowy.data.repositories.subject

import io.github.wulkanowy.data.db.dao.SubjectDao
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Subject
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectLocal @Inject constructor(private val subjectDao: SubjectDao) {

    fun getSubjects(semester: Semester): Flow<List<Subject>> {
        return subjectDao.loadAll(semester.diaryId, semester.studentId)
    }

    suspend fun saveSubjects(subjects: List<Subject>) {
        subjectDao.insertAll(subjects)
    }

    suspend fun deleteSubjects(subjects: List<Subject>) {
        subjectDao.deleteAll(subjects)
    }
}
