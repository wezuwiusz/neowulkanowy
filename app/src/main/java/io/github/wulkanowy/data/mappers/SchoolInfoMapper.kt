package io.github.wulkanowy.data.mappers

import io.github.wulkanowy.data.db.entities.School
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.sdk.pojo.School as SdkSchool

fun SdkSchool.mapToEntity(semester: Semester) = School(
    studentId = semester.studentId,
    classId = semester.classId,
    name = name,
    address = address,
    contact = contact,
    headmaster = headmaster,
    pedagogue = pedagogue
)
