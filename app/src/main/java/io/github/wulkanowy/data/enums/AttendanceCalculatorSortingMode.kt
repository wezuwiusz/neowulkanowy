package io.github.wulkanowy.data.enums

enum class AttendanceCalculatorSortingMode(private val value: String) {
    ALPHABETIC("alphabetic"),
    ATTENDANCE("attendance_percentage"),
    LESSON_BALANCE("lesson_balance");

    companion object {
        fun getByValue(value: String) =
            AttendanceCalculatorSortingMode.values()
                .find { it.value == value } ?: ALPHABETIC
    }
}
