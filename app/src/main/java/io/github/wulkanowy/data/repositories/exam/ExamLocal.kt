package io.github.wulkanowy.data.repositories.exam

import io.github.wulkanowy.data.db.dao.ExamDao
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.data.db.entities.Semester
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExamLocal @Inject constructor(private val examDb: ExamDao) {

    fun getExams(semester: Semester, startDate: LocalDate, endDate: LocalDate): Flow<List<Exam>> {
        return examDb.loadAll(semester.diaryId, semester.studentId, startDate, endDate)
    }

    suspend fun saveExams(exams: List<Exam>) {
        examDb.insertAll(exams)
    }

    suspend fun deleteExams(exams: List<Exam>) {
        examDb.deleteAll(exams)
    }
}
