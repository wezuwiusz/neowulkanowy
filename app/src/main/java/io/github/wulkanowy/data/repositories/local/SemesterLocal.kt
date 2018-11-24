package io.github.wulkanowy.data.repositories.local

import io.github.wulkanowy.data.db.dao.SemesterDao
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.reactivex.Maybe
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SemesterLocal @Inject constructor(private val semesterDb: SemesterDao) {

    fun saveSemesters(semesters: List<Semester>) {
        return semesterDb.insertAll(semesters)
    }

    fun getSemesters(student: Student): Maybe<List<Semester>> {
        return semesterDb.load(student.studentId).filter { !it.isEmpty() }
    }

    fun setCurrentSemester(semester: Semester) {
        semesterDb.run {
            resetCurrent(semester.studentId)
            update(semester.semesterId, semester.diaryId)
        }
    }
}
