package io.github.wulkanowy.data.mappers

import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.data.db.entities.Student
import java.time.LocalDate
import io.github.wulkanowy.sdk.pojo.LuckyNumber as SdkLuckyNumber

fun SdkLuckyNumber.mapToEntity(student: Student) = LuckyNumber(
    studentId = student.studentId,
    date = LocalDate.now(),
    luckyNumber = number
)
