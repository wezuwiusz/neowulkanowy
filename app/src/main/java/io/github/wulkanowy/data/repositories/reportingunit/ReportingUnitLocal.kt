package io.github.wulkanowy.data.repositories.reportingunit

import io.github.wulkanowy.data.db.dao.ReportingUnitDao
import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.github.wulkanowy.data.db.entities.Student
import io.reactivex.Maybe
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportingUnitLocal @Inject constructor(private val reportingUnitDb: ReportingUnitDao) {

    fun getReportingUnits(student: Student): Maybe<List<ReportingUnit>> {
        return reportingUnitDb.load(student.studentId).filter { it.isNotEmpty() }
    }

    fun getReportingUnit(student: Student, unitId: Int): Maybe<ReportingUnit> {
        return reportingUnitDb.loadOne(student.studentId, unitId)
    }

    fun saveReportingUnits(reportingUnits: List<ReportingUnit>): List<Long> {
        return reportingUnitDb.insertAll(reportingUnits)
    }

    fun deleteReportingUnits(reportingUnits: List<ReportingUnit>) {
        reportingUnitDb.deleteAll(reportingUnits)
    }
}
