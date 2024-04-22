package io.github.wulkanowy.ui.modules.grade.details

enum class ViewType(val id: Int) {
    HEADER(1),
    ITEM(2)
}

data class GradeDetailsItem(
    val value: Any,
    val viewType: ViewType
)

data class GradeDetailsHeader(
    val subject: String,
    val average: Double?,
    val averageAllYear: Double?,
    val pointsSum: String?,
    val grades: List<GradeDetailsItem>
) {
    var newGrades = 0
}
