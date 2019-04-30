package io.github.wulkanowy.data.repositories.gradestatistics

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.GradeStatistics
import io.github.wulkanowy.data.db.entities.Semester
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

    fun getGradesStatistics(semester: Semester, subjectName: String, isSemester: Boolean, forceRefresh: Boolean = false): Single<List<GradeStatistics>> {
        return local.getGradesStatistics(semester, isSemester, subjectName).filter { !forceRefresh }
            .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                .flatMap {
                    if (it) remote.getGradeStatistics(semester, isSemester)
                    else Single.error(UnknownHostException())
                }.flatMap { new ->
                    local.getGradesStatistics(semester, isSemester).toSingle(emptyList())
                        .doOnSuccess { old ->
                            local.deleteGradesStatistics(old.uniqueSubtract(new))
                            local.saveGradesStatistics(new.uniqueSubtract(old))
                        }
                }.flatMap { local.getGradesStatistics(semester, isSemester, subjectName).toSingle(emptyList()) })
    }
}
