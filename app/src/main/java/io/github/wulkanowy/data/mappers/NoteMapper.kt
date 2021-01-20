package io.github.wulkanowy.data.mappers

import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.sdk.pojo.Note as SdkNote

fun List<SdkNote>.mapToEntities(semester: Semester) = map {
    Note(
        studentId = semester.studentId,
        date = it.date,
        teacher = it.teacher,
        teacherSymbol = it.teacherSymbol,
        category = it.category,
        categoryType = it.categoryType.id,
        isPointsShow = it.showPoints,
        points = it.points,
        content = it.content
    )
}
