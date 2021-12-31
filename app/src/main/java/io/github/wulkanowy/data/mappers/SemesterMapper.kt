package io.github.wulkanowy.data.mappers

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.sdk.pojo.Semester as SdkSemester

fun List<SdkSemester>.mapToEntities(studentId: Int) = map {
    Semester(
        studentId = studentId,
        diaryId = it.diaryId,
        kindergartenDiaryId = it.kindergartenDiaryId,
        diaryName = it.diaryName,
        schoolYear = it.schoolYear,
        semesterId = it.semesterId,
        semesterName = it.semesterNumber,
        start = it.start,
        end = it.end,
        classId = it.classId,
        unitId = it.unitId
    )
}
