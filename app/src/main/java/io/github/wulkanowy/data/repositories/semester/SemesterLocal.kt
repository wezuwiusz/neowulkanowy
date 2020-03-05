package io.github.wulkanowy.data.repositories.semester

import io.github.wulkanowy.data.db.dao.SemesterDao
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.reactivex.Maybe
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SemesterLocal @Inject constructor(private val semesterDb: SemesterDao) {

    fun saveSemesters(semesters: List<Semester>) {
        semesterDb.insertAll(semesters)
    }

    fun deleteSemesters(semesters: List<Semester>) {
        semesterDb.deleteAll(semesters)
    }

    fun getSemesters(student: Student): Maybe<List<Semester>> {
        return semesterDb.loadAll(student.studentId, student.classId).filter { it.isNotEmpty() }
    }
}
