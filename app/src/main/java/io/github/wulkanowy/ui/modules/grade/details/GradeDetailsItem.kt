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
    val number: Int,
    val average: Double?,
    val pointsSum: String?,
    var newGrades: Int,
    val grades: List<GradeDetailsItem>
)
