package io.github.wulkanowy.data.repositories.gradestatistics

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.GradePointsStatistics
import io.github.wulkanowy.data.db.entities.GradeStatistics
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.GradeStatisticsItem
import io.github.wulkanowy.ui.modules.grade.statistics.ViewType
import io.github.wulkanowy.utils.uniqueSubtract
import io.reactivex.Single
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradeStatisticsRepository @Inject constructor(
    private val settings: InternetObservingSettings,
    private val local: GradeStatisticsLocal,
    private val remote: GradeStatisticsRemote
) {

    fun getGradesStatistics(student: Student, semester: Semester, subjectName: String, isSemester: Boolean, forceRefresh: Boolean = false): Single<List<GradeStatisticsItem>> {
        return local.getGradesStatistics(semester, isSemester, subjectName).map { it.mapToStatisticItems() }.filter { !forceRefresh }
            .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                .flatMap {
                    if (it) remote.getGradeStatistics(student, semester, isSemester)
                    else Single.error(UnknownHostException())
                }.flatMap { new ->
                    local.getGradesStatistics(semester, isSemester).toSingle(emptyList())
                        .doOnSuccess { old ->
                            local.deleteGradesStatistics(old.uniqueSubtract(new))
                            local.saveGradesStatistics(new.uniqueSubtract(old))
                        }
                }.flatMap { local.getGradesStatistics(semester, isSemester, subjectName).map { it.mapToStatisticItems() }.toSingle(emptyList()) })
    }

    fun getGradesPointsStatistics(student: Student, semester: Semester, subjectName: String, forceRefresh: Boolean): Single<List<GradeStatisticsItem>> {
        return local.getGradesPointsStatistics(semester, subjectName).map { it.mapToStatisticsItem() }.filter { !forceRefresh }
            .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                .flatMap {
                    if (it) remote.getGradePointsStatistics(student, semester)
                    else Single.error(UnknownHostException())
                }.flatMap { new ->
                    local.getGradesPointsStatistics(semester).toSingle(emptyList())
                        .doOnSuccess { old ->
                            local.deleteGradesPointsStatistics(old.uniqueSubtract(new))
                            local.saveGradesPointsStatistics(new.uniqueSubtract(old))
                        }
                }.flatMap { local.getGradesPointsStatistics(semester, subjectName).map { it.mapToStatisticsItem() }.toSingle(emptyList()) })
    }

    private fun List<GradeStatistics>.mapToStatisticItems(): List<GradeStatisticsItem> {
        return groupBy { it.subject }.map {
            GradeStatisticsItem(
                type = ViewType.PARTIAL,
                partial = it.value
                    .sortedByDescending { item -> item.grade }
                    .filter { item -> item.amount != 0 },
                points = null
            )
        }
    }

    private fun List<GradePointsStatistics>.mapToStatisticsItem(): List<GradeStatisticsItem> {
        return map {
            GradeStatisticsItem(
                type = ViewType.POINTS,
                partial = emptyList(),
                points = it
            )
        }
    }
}
