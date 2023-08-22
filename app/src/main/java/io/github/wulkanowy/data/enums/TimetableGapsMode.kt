package io.github.wulkanowy.data.enums

enum class TimetableGapsMode(val value: String) {
    NO_GAPS("no_gaps"),
    BETWEEN_LESSONS("between"),
    BETWEEN_AND_BEFORE_LESSONS("before_and_between");

    companion object {
        fun getByValue(value: String) = entries.find { it.value == value } ?: BETWEEN_LESSONS
    }
}
