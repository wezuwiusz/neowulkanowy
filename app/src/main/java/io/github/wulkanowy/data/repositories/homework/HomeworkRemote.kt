package io.github.wulkanowy.data.repositories.homework

import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.sdk.Sdk
import io.reactivex.Single
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeworkRemote @Inject constructor(private val sdk: Sdk) {

    fun getHomework(semester: Semester, startDate: LocalDate, endDate: LocalDate): Single<List<Homework>> {
        return sdk.switchDiary(semester.diaryId, semester.schoolYear).getHomework(startDate, endDate)
            .map { homework ->
                homework.map {
                    Homework(
                        semesterId = semester.semesterId,
                        studentId = semester.studentId,
                        date = it.date,
                        entryDate = it.entryDate,
                        subject = it.subject,
                        content = it.content,
                        teacher = it.teacher,
                        teacherSymbol = it.teacherSymbol
                    )
                }
            }
    }
}
