package io.github.wulkanowy.ui.modules.grade

enum class GradeSortingMode(val value: String) {
    ALPHABETIC("alphabetic"),
    DATE("date");

    companion object {
        fun getByValue(value: String) = values().firstOrNull { it.value == value } ?: ALPHABETIC
    }
}