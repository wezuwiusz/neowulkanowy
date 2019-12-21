package io.github.wulkanowy.data.repositories.grade

import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.sdk.Sdk
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradeRemote @Inject constructor(private val sdk: Sdk) {

    fun getGrades(semester: Semester): Single<List<Grade>> {
        return sdk.switchDiary(semester.diaryId, semester.schoolYear).getGrades(semester.semesterId)
            .map { grades ->
                grades.map {
                    Grade(
                        studentId = semester.studentId,
                        semesterId = semester.semesterId,
                        subject = it.subject,
                        entry = it.entry,
                        value = it.value,
                        modifier = it.modifier,
                        comment = it.comment,
                        color = it.color,
                        gradeSymbol = it.symbol,
                        description = it.description,
                        weight = it.weight,
                        weightValue = it.weightValue,
                        date = it.date,
                        teacher = it.teacher
                    )
                }
            }
    }
}
