package io.github.wulkanowy.data.repositories.local

import io.github.wulkanowy.data.db.dao.SubjectDao
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Subject
import io.reactivex.Maybe
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectLocal @Inject constructor(private val subjectDao: SubjectDao) {

    fun getSubjects(semester: Semester): Maybe<List<Subject>> {
        return subjectDao.loadAll(semester.diaryId, semester.studentId)
            .filter { !it.isEmpty() }
    }

    fun saveSubjects(subjects: List<Subject>) {
        subjectDao.insertAll(subjects)
    }

    fun deleteSubjects(subjects: List<Subject>) {
        subjectDao.deleteAll(subjects)
    }
}
