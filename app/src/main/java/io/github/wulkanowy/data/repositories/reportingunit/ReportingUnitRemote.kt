package io.github.wulkanowy.data.repositories.reportingunit

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportingUnitRemote @Inject constructor(private val api: Api) {

    fun getReportingUnits(): Single<List<ReportingUnit>> {
        return api.getReportingUnits().map {
            it.map { unit ->
                ReportingUnit(
                    studentId = api.studentId,
                    realId = unit.id,
                    roles = unit.roles,
                    senderId = unit.senderId,
                    senderName = unit.senderName,
                    shortName = unit.short
                )
            }
        }
    }
}
