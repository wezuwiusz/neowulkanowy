package io.github.wulkanowy.data.repositories.reportingunit

import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.uniqueSubtract
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportingUnitRepository @Inject constructor(
    private val local: ReportingUnitLocal,
    private val remote: ReportingUnitRemote
) {

    suspend fun refreshReportingUnits(student: Student) {
        val new = remote.getReportingUnits(student)
        val old = local.getReportingUnits(student)

        local.deleteReportingUnits(old.uniqueSubtract(new))
        local.saveReportingUnits(new.uniqueSubtract(old))
    }

    suspend fun getReportingUnits(student: Student): List<ReportingUnit> {
        return local.getReportingUnits(student).ifEmpty {
            refreshReportingUnits(student)

            local.getReportingUnits(student)
        }
    }

    suspend fun getReportingUnit(student: Student, unitId: Int): ReportingUnit? {
        return local.getReportingUnit(student, unitId) ?: run {
            refreshReportingUnits(student)

            return local.getReportingUnit(student, unitId)
        }
    }
}
