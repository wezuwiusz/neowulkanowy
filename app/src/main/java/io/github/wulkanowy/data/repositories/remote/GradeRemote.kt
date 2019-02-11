package io.github.wulkanowy.data.repositories.remote

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.utils.toLocalDate
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradeRemote @Inject constructor(private val api: Api) {

    fun getGrades(semester: Semester): Single<List<Grade>> {
        return Single.just(api.apply { diaryId = semester.diaryId })
            .flatMap { it.getGrades(semester.semesterId) }
            .map { grades ->
                grades.map {
                    Grade(
                        semesterId = semester.semesterId,
                        studentId = semester.studentId,
                        subject = it.subject,
                        entry = it.entry,
                        value = it.value,
                        modifier = it.modifier,
                        comment = it.comment,
                        color = it.color,
                        gradeSymbol = it.symbol.orEmpty(),
                        description = it.description.orEmpty(),
                        weight = it.weight,
                        weightValue = it.weightValue,
                        date = it.date.toLocalDate(),
                        teacher = it.teacher
                    )
                }
            }
    }
}
