package io.github.wulkanowy.data.repositories.exam

import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.sdk.Sdk
import io.reactivex.Single
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExamRemote @Inject constructor(private val sdk: Sdk) {

    fun getExams(semester: Semester, startDate: LocalDate, endDate: LocalDate): Single<List<Exam>> {
        return sdk.switchDiary(semester.diaryId, semester.schoolYear).getExams(startDate, endDate, semester.semesterId)
            .map { exams ->
                exams.map {
                    Exam(
                        studentId = semester.studentId,
                        diaryId = semester.diaryId,
                        date = it.date,
                        entryDate = it.entryDate,
                        subject = it.subject,
                        group = it.group,
                        type = it.type,
                        description = it.description,
                        teacher = it.teacher,
                        teacherSymbol = it.teacherSymbol
                    )
                }
            }
    }
}
