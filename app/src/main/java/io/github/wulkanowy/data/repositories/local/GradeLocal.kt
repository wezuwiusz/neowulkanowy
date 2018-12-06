package io.github.wulkanowy.data.repositories.local

import io.github.wulkanowy.data.db.dao.GradeDao
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Completable
import io.reactivex.Maybe
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradeLocal @Inject constructor(private val gradeDb: GradeDao) {

    fun getGrades(semester: Semester): Maybe<List<Grade>> {
        return gradeDb.load(semester.semesterId, semester.studentId).filter { !it.isEmpty() }
    }

    fun getNewGrades(semester: Semester): Maybe<List<Grade>> {
        return gradeDb.loadNew(semester.semesterId, semester.studentId)
    }

    fun saveGrades(grades: List<Grade>) {
        gradeDb.insertAll(grades)
    }

    fun updateGrade(grade: Grade): Completable {
        return Completable.fromCallable { gradeDb.update(grade) }
    }

    fun updateGrades(grades: List<Grade>): Completable {
        return Completable.fromCallable { gradeDb.updateAll(grades) }
    }

    fun deleteGrades(grades: List<Grade>) {
        gradeDb.deleteAll(grades)
    }
}
