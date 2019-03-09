package io.github.wulkanowy.data.repositories.grade

import io.github.wulkanowy.data.db.dao.GradeDao
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Maybe
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradeLocal @Inject constructor(private val gradeDb: GradeDao) {

    fun saveGrades(grades: List<Grade>) {
        gradeDb.insertAll(grades)
    }

    fun deleteGrades(grades: List<Grade>) {
        gradeDb.deleteAll(grades)
    }

    fun updateGrades(grades: List<Grade>) {
        gradeDb.updateAll(grades)
    }

    fun getGrades(semester: Semester): Maybe<List<Grade>> {
        return gradeDb.loadAll(semester.semesterId, semester.studentId).filter { it.isNotEmpty() }
    }
}
