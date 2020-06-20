package io.github.wulkanowy.data.repositories.exam

import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.sunday
import io.github.wulkanowy.utils.uniqueSubtract
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExamRepository @Inject constructor(
    private val local: ExamLocal,
    private val remote: ExamRemote
) {

    suspend fun getExams(student: Student, semester: Semester, start: LocalDate, end: LocalDate, forceRefresh: Boolean = false): List<Exam> {
        return local.getExams(semester, start.monday, end.sunday).filter { !forceRefresh }.ifEmpty {
            val new = remote.getExams(student, semester, start.monday, end.sunday)
            val old = local.getExams(semester, start.monday, end.sunday)

            local.deleteExams(old.uniqueSubtract(new))
            local.saveExams(new.uniqueSubtract(old))

            local.getExams(semester, start.monday, end.sunday)
        }.filter { it.date in start..end }
    }
}
