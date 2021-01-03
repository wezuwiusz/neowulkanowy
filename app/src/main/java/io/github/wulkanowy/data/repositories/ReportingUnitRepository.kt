package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.ReportingUnitDao
import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.init
import io.github.wulkanowy.utils.uniqueSubtract
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportingUnitRepository @Inject constructor(
    private val reportingUnitDb: ReportingUnitDao,
    private val sdk: Sdk
) {

    suspend fun refreshReportingUnits(student: Student) {
        val new = sdk.init(student).getReportingUnits().mapToEntities(student)
        val old = reportingUnitDb.load(student.studentId)

        reportingUnitDb.deleteAll(old.uniqueSubtract(new))
        reportingUnitDb.insertAll(new.uniqueSubtract(old))
    }

    suspend fun getReportingUnits(student: Student): List<ReportingUnit> {
        return reportingUnitDb.load(student.studentId).ifEmpty {
            refreshReportingUnits(student)

            reportingUnitDb.load(student.studentId)
        }
    }

    suspend fun getReportingUnit(student: Student, unitId: Int): ReportingUnit? {
        return reportingUnitDb.loadOne(student.studentId, unitId) ?: run {
            refreshReportingUnits(student)

            return reportingUnitDb.loadOne(student.studentId, unitId)
        }
    }
}
