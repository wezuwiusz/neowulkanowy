package io.github.wulkanowy.data.repositories

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.GradeSummary
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.repositories.local.GradeSummaryLocal
import io.github.wulkanowy.data.repositories.remote.GradeSummaryRemote
import io.reactivex.Single
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradeSummaryRepository @Inject constructor(
        private val settings: InternetObservingSettings,
        private val local: GradeSummaryLocal,
        private val remote: GradeSummaryRemote
) {

    fun getGradesSummary(semester: Semester, forceRefresh: Boolean = false): Single<List<GradeSummary>> {
        return local.getGradesSummary(semester).filter { !forceRefresh }
                .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                        .flatMap {
                            if (it) remote.getGradeSummary(semester)
                            else Single.error(UnknownHostException())
                        }.flatMap { newGradesSummary ->
                            local.getGradesSummary(semester).toSingle(emptyList())
                                    .doOnSuccess { oldGradesSummary ->
                                        local.deleteGradesSummary(oldGradesSummary - newGradesSummary)
                                        local.saveGradesSummary(newGradesSummary - oldGradesSummary)
                                    }
                        }.flatMap { local.getGradesSummary(semester).toSingle(emptyList()) })
    }
}
