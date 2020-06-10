package io.github.wulkanowy.ui.modules.grade

enum class GradeAverageMode(val value: String) {
    ALL_YEAR("all_year"),
    ONE_SEMESTER("only_one_semester"),
    BOTH_SEMESTERS("both_semesters");

    companion object {
        fun getByValue(value: String) = values().firstOrNull { it.value == value } ?: ONE_SEMESTER
    }
}
