package io.github.wulkanowy.data.repositories.reportingunit

import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.init
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportingUnitRemote @Inject constructor(private val sdk: Sdk) {

    suspend fun getReportingUnits(student: Student): List<ReportingUnit> {
        return sdk.init(student).getReportingUnits().map { unit ->
            ReportingUnit(
                studentId = sdk.studentId,
                realId = unit.id,
                roles = unit.roles,
                senderId = unit.senderId,
                senderName = unit.senderName,
                shortName = unit.short
            )
        }
    }
}
