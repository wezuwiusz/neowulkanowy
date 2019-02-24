package io.github.wulkanowy.data.repositories.reportingunit

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.ApiHelper
import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.github.wulkanowy.data.db.entities.Student
import io.reactivex.Maybe
import io.reactivex.Single
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportingUnitRepository @Inject constructor(
    private val settings: InternetObservingSettings,
    private val local: ReportingUnitLocal,
    private val remote: ReportingUnitRemote,
    private val apiHelper: ApiHelper
) {

    fun getReportingUnits(student: Student, forceRefresh: Boolean = false): Single<List<ReportingUnit>> {
        return Single.just(apiHelper.initApi(student))
            .flatMap { _ ->
                local.getReportingUnits(student).filter { !forceRefresh }
                    .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                        .flatMap {
                            if (it) remote.getReportingUnits()
                            else Single.error(UnknownHostException())
                        }.flatMap { new ->
                            local.getReportingUnits(student).toSingle(emptyList())
                                .doOnSuccess { old ->
                                    local.deleteReportingUnits(old - new)
                                    local.saveReportingUnits(new - old)
                                }
                        }.flatMap { local.getReportingUnits(student).toSingle(emptyList()) }
                    )
            }
    }

    fun getReportingUnit(student: Student, unitId: Int): Maybe<ReportingUnit> {
        return Maybe.just(apiHelper.initApi(student))
            .flatMap { _ ->
                local.getReportingUnit(student, unitId)
                    .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                        .flatMap {
                            if (it) getReportingUnits(student, true)
                            else Single.error(UnknownHostException())
                        }.flatMapMaybe {
                            local.getReportingUnit(student, unitId)
                        }
                    )
            }
    }
}
