package io.github.wulkanowy.data.repositories.exam

import io.github.wulkanowy.data.db.dao.ExamDao
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Maybe
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExamLocal @Inject constructor(private val examDb: ExamDao) {

    fun getExams(semester: Semester, startDate: LocalDate, endDate: LocalDate): Maybe<List<Exam>> {
        return examDb.loadAll(semester.diaryId, semester.studentId, startDate, endDate)
                .filter { !it.isEmpty() }
    }

    fun saveExams(exams: List<Exam>) {
        examDb.insertAll(exams)
    }

    fun deleteExams(exams: List<Exam>) {
        examDb.deleteAll(exams)
    }
}
