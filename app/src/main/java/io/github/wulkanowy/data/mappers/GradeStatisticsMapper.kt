package io.github.wulkanowy.data.mappers

import io.github.wulkanowy.data.db.entities.GradePartialStatistics
import io.github.wulkanowy.data.db.entities.GradePointsStatistics
import io.github.wulkanowy.data.db.entities.GradeSemesterStatistics
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.pojos.GradeStatisticsItem
import io.github.wulkanowy.ui.modules.grade.statistics.ViewType
import io.github.wulkanowy.sdk.pojo.GradeStatisticsSubject as SdkGradeStatisticsSubject
import io.github.wulkanowy.sdk.pojo.GradeStatisticsSemester as SdkGradeStatisticsSemester
import io.github.wulkanowy.sdk.pojo.GradePointsStatistics as SdkGradePointsStatistics

@JvmName("mapToEntitiesSubject")
fun List<SdkGradeStatisticsSubject>.mapToEntities(semester: Semester) = map {
    GradePartialStatistics(
        semesterId = semester.semesterId,
        studentId = semester.studentId,
        subject = it.subject,
        classAverage = it.classAverage,
        studentAverage = it.studentAverage,
        classAmounts = it.classItems
            .sortedBy { item -> item.grade }
            .map { item -> item.amount },
        studentAmounts = it.studentItems.map { item -> item.amount }
    )
}

@JvmName("mapToEntitiesSemester")
fun List<SdkGradeStatisticsSemester>.mapToEntities(semester: Semester) = map {
    GradeSemesterStatistics(
        semesterId = semester.semesterId,
        studentId = semester.studentId,
        subject = it.subject,
        amounts = it.items
            .sortedBy { item -> item.grade }
            .map { item -> item.amount },
        studentGrade = it.items.singleOrNull { item -> item.isStudentHere }?.grade ?: 0
    )
}

@JvmName("mapToEntitiesPoints")
fun List<SdkGradePointsStatistics>.mapToEntities(semester: Semester) = map {
    GradePointsStatistics(
        semesterId = semester.semesterId,
        studentId = semester.studentId,
        subject = it.subject,
        others = it.others,
        student = it.student
    )
}

fun List<GradePartialStatistics>.mapPartialToStatisticItems() = filterNot { it.classAmounts.isEmpty() }.map {
    GradeStatisticsItem(
        type = ViewType.PARTIAL,
        average = it.classAverage,
        partial = it,
        points = null,
        semester = null
    )
}

fun List<GradeSemesterStatistics>.mapSemesterToStatisticItems() = filterNot { it.amounts.isEmpty() }.map {
    GradeStatisticsItem(
        type = ViewType.SEMESTER,
        partial = null,
        points = null,
        average = "",
        semester = it
    )
}

fun List<GradePointsStatistics>.mapPointsToStatisticsItems() = map {
    GradeStatisticsItem(
        type = ViewType.POINTS,
        partial = null,
        semester = null,
        average = "",
        points = it
    )
}
