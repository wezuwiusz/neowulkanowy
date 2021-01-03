package io.github.wulkanowy.data.mappers

import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.pojo.Recipient as SdkRecipient

fun List<SdkRecipient>.mapToEntities(student: Student) = map {
    Recipient(
        studentId = student.studentId,
        realId = it.id,
        realName = it.name,
        name = it.shortName,
        hash = it.hash,
        loginId = it.loginId,
        role = it.role,
        unitId = it.reportingUnitId ?: 0
    )
}
