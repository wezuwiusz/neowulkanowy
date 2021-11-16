package io.github.wulkanowy.ui.modules.grade

enum class GradeExpandMode(val value: String) {
    ONE("one"), UNLIMITED("any"), ALWAYS_EXPANDED("always");

    companion object {
        fun getByValue(value: String) = values().firstOrNull { it.value == value } ?: ONE
    }
}