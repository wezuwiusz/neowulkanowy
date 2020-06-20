package io.github.wulkanowy.data.repositories.semester

import io.github.wulkanowy.data.db.dao.SemesterDao
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SemesterLocal @Inject constructor(private val semesterDb: SemesterDao) {

    suspend fun saveSemesters(semesters: List<Semester>) {
        semesterDb.insertAll(semesters)
    }

    suspend fun deleteSemesters(semesters: List<Semester>) {
        semesterDb.deleteAll(semesters)
    }

    suspend fun getSemesters(student: Student): List<Semester> {
        return semesterDb.loadAll(student.studentId, student.classId)
    }
}
