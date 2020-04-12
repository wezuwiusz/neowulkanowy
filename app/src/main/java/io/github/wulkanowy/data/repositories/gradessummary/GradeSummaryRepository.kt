package io.github.wulkanowy.data.repositories.gradessummary

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.GradeSummary
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.uniqueSubtract
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

    fun getGradesSummary(student: Student, semester: Semester, forceRefresh: Boolean = false): Single<List<GradeSummary>> {
        return local.getGradesSummary(semester).filter { !forceRefresh }
            .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                .flatMap {
                    if (it) remote.getGradeSummary(student, semester)
                    else Single.error(UnknownHostException())
                }.flatMap { new ->
                    local.getGradesSummary(semester).toSingle(emptyList())
                        .doOnSuccess { old ->
                            local.deleteGradesSummary(old.uniqueSubtract(new))
                            local.saveGradesSummary(new.uniqueSubtract(old))
                        }
                }.flatMap { local.getGradesSummary(semester).toSingle(emptyList()) })
    }
}
