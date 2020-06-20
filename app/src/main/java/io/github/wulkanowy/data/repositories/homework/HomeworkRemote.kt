package io.github.wulkanowy.data.repositories.homework

import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.init
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeworkRemote @Inject constructor(private val sdk: Sdk) {

    suspend fun getHomework(student: Student, semester: Semester, startDate: LocalDate, endDate: LocalDate): List<Homework> {
        return sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
            .getHomework(startDate, endDate)
            .map {
                Homework(
                    semesterId = semester.semesterId,
                    studentId = semester.studentId,
                    date = it.date,
                    entryDate = it.entryDate,
                    subject = it.subject,
                    content = it.content,
                    teacher = it.teacher,
                    teacherSymbol = it.teacherSymbol,
                    attachments = it.attachments.map { attachment -> attachment.url to attachment.name }
                )
            }
    }
}
