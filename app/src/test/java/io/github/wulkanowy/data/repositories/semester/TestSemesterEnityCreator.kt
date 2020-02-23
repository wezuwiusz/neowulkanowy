package io.github.wulkanowy.data.repositories.semester

import io.github.wulkanowy.data.db.entities.Semester
import org.threeten.bp.LocalDate

fun createSemesterEntity(diaryId: Int, semesterId: Int, start: LocalDate, end: LocalDate, semesterName: Int = 1): Semester {
    return Semester(
        studentId = 1,
        diaryId = diaryId,
        semesterId = semesterId,
        diaryName = "$semesterId",
        schoolYear = 1970,
        classId = 0,
        semesterName = semesterName,
        unitId = 1,
        start = start,
        end = end
    )
}
