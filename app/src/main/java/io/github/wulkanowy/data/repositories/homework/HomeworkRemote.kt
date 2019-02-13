package io.github.wulkanowy.data.repositories.homework

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.utils.toLocalDate
import io.reactivex.Single
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeworkRemote @Inject constructor(private val api: Api) {

    fun getHomework(semester: Semester, date: LocalDate): Single<List<Homework>> {
        return Single.just(api.apply { diaryId = semester.diaryId })
            .flatMap { it.getHomework(date, date) }
            .map { homework ->
                homework.map {
                    Homework(
                        semesterId = semester.semesterId,
                        studentId = semester.studentId,
                        date = it.date.toLocalDate(),
                        entryDate = it.entryDate.toLocalDate(),
                        subject = it.subject,
                        content = it.content,
                        teacher = it.teacher,
                        teacherSymbol = it.teacherSymbol
                    )
                }
            }
    }
}
