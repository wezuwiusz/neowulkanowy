package io.github.wulkanowy.data.repositories.remote

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.data.db.entities.GradeSummary
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradeSummaryRemote @Inject constructor(private val api: Api) {

    fun getGradeSummary(semester: Semester): Single<List<GradeSummary>> {
        return Single.just(api.run {
            if (diaryId != semester.diaryId) {
                diaryId = semester.diaryId
                notifyDataChanged()
            }
        }).flatMap { api.getGradesSummary(semester.semesterId.toInt()) }
                .map { gradesSummary ->
                    gradesSummary.map {
                        GradeSummary(
                                semesterId = semester.semesterId,
                                studentId = semester.studentId,
                                subject = it.name,
                                predictedGrade = it.predicted,
                                finalGrade = it.final
                        )
                    }
                }
    }
}
