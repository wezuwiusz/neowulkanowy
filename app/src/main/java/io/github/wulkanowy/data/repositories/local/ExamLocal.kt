package io.github.wulkanowy.data.repositories.local

import io.github.wulkanowy.data.db.dao.ExamDao
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.utils.extension.toDate
import io.reactivex.Maybe
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.TemporalAdjusters
import javax.inject.Inject

class ExamLocal @Inject constructor(private val examDb: ExamDao) {

    fun getExams(semester: Semester, startDate: LocalDate, endDate: LocalDate): Maybe<List<Exam>> {
        return examDb.getExams(semester.diaryId, semester.studentId, startDate, endDate)
                .filter { !it.isEmpty() }
    }

    fun saveExams(exams: List<Exam>) {
        examDb.insertAll(exams)
    }

    fun deleteExams(exams: List<Exam>) {
        examDb.deleteAll(exams)
    }
}
