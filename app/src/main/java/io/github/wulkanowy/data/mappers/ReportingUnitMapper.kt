package io.github.wulkanowy.data.mappers

import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.pojo.ReportingUnit as SdkReportingUnit

fun List<SdkReportingUnit>.mapToEntities(student: Student) = map {
    ReportingUnit(
        studentId = student.studentId,
        unitId = it.id,
        roles = it.roles,
        senderId = it.senderId,
        senderName = it.senderName,
        shortName = it.short
    )
}
