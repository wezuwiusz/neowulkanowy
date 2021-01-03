package io.github.wulkanowy.data.mappers

import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.GradeSummary
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.sdk.pojo.GradeSummary as SdkGradeSummary
import io.github.wulkanowy.sdk.pojo.Grade as SdkGrade

fun List<SdkGrade>.mapToEntities(semester: Semester) = map {
    Grade(
        studentId = semester.studentId,
        semesterId = semester.semesterId,
        subject = it.subject,
        entry = it.entry,
        value = it.value,
        modifier = it.modifier,
        comment = it.comment,
        color = it.color,
        gradeSymbol = it.symbol,
        description = it.description,
        weight = it.weight,
        weightValue = it.weightValue,
        date = it.date,
        teacher = it.teacher
    )
}

@JvmName("mapGradeSummaryToEntities")
fun List<SdkGradeSummary>.mapToEntities(semester: Semester) = map {
    GradeSummary(
        semesterId = semester.semesterId,
        studentId = semester.studentId,
        position = 0,
        subject = it.name,
        predictedGrade = it.predicted,
        finalGrade = it.final,
        pointsSum = it.pointsSum,
        proposedPoints = it.proposedPoints,
        finalPoints = it.finalPoints,
        average = it.average
    )
}
