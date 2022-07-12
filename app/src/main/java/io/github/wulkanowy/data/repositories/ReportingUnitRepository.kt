package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.ReportingUnitDao
import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.AutoRefreshHelper
import io.github.wulkanowy.utils.getRefreshKey
import io.github.wulkanowy.utils.init
import io.github.wulkanowy.utils.uniqueSubtract
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportingUnitRepository @Inject constructor(
    private val reportingUnitDb: ReportingUnitDao,
    private val sdk: Sdk,
    private val refreshHelper: AutoRefreshHelper,
) {

    private val cacheKey = "reporting_unit"

    suspend fun refreshReportingUnits(student: Student) {
        val new = sdk.init(student).getReportingUnits().mapToEntities(student)
        val old = reportingUnitDb.load(student.id.toInt())

        reportingUnitDb.deleteAll(old.uniqueSubtract(new))
        reportingUnitDb.insertAll(new.uniqueSubtract(old))

        refreshHelper.updateLastRefreshTimestamp(getRefreshKey(cacheKey, student))
    }

    suspend fun getReportingUnits(student: Student): List<ReportingUnit> {
        val cached = reportingUnitDb.load(student.id.toInt())
        val isExpired = refreshHelper.shouldBeRefreshed(getRefreshKey(cacheKey, student))

        return if (cached.isEmpty() || isExpired) {
            refreshReportingUnits(student)
            reportingUnitDb.load(student.id.toInt())
        } else cached
    }

    suspend fun getReportingUnit(student: Student, unitId: Int): ReportingUnit? {
        val cached = reportingUnitDb.loadOne(student.id.toInt(), unitId)
        val isExpired = refreshHelper.shouldBeRefreshed(getRefreshKey(cacheKey, student))

        return if (cached == null || isExpired) {
            refreshReportingUnits(student)
            reportingUnitDb.loadOne(student.id.toInt(), unitId)
        } else cached
    }
}
