package io.github.wulkanowy.data.repositories.reportingunit

import io.github.wulkanowy.data.db.dao.ReportingUnitDao
import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.github.wulkanowy.data.db.entities.Student
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportingUnitLocal @Inject constructor(private val reportingUnitDb: ReportingUnitDao) {

    suspend fun getReportingUnits(student: Student): List<ReportingUnit> {
        return reportingUnitDb.load(student.studentId)
    }

    suspend fun getReportingUnit(student: Student, unitId: Int): ReportingUnit? {
        return reportingUnitDb.loadOne(student.studentId, unitId)
    }

    suspend fun saveReportingUnits(reportingUnits: List<ReportingUnit>): List<Long> {
        return reportingUnitDb.insertAll(reportingUnits)
    }

    suspend fun deleteReportingUnits(reportingUnits: List<ReportingUnit>) {
        reportingUnitDb.deleteAll(reportingUnits)
    }
}
