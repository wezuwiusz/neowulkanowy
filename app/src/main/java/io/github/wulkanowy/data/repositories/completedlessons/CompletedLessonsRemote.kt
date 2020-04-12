package io.github.wulkanowy.data.repositories.completedlessons

import io.github.wulkanowy.data.db.entities.CompletedLesson
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.init
import io.reactivex.Single
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompletedLessonsRemote @Inject constructor(private val sdk: Sdk) {

    fun getCompletedLessons(student: Student, semester: Semester, startDate: LocalDate, endDate: LocalDate): Single<List<CompletedLesson>> {
        return sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
            .getCompletedLessons(startDate, endDate)
            .map { lessons ->
                lessons.map {
                    it.absence
                    CompletedLesson(
                        studentId = semester.studentId,
                        diaryId = semester.diaryId,
                        date = it.date,
                        number = it.number,
                        subject = it.subject,
                        topic = it.topic,
                        teacher = it.teacher,
                        teacherSymbol = it.teacherSymbol,
                        substitution = it.substitution,
                        absence = it.absence,
                        resources = it.resources
                    )
                }
            }
    }
}
