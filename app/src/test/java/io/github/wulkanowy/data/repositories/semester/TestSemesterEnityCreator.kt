package io.github.wulkanowy.data.repositories.semester

import io.github.wulkanowy.data.db.entities.Semester

fun createSemesterEntity(current: Boolean): Semester {
    return Semester(
        studentId = 0,
        diaryId = 0,
        semesterId = 0,
        diaryName = "",
        classId = 0,
        isCurrent = current,
        semesterName = 0,
        unitId = 0
    )
}
