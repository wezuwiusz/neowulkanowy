package io.github.wulkanowy.data.enums

import java.io.Serializable

enum class GradeColorTheme(val value: String) : Serializable {
    VULCAN("vulcan"),
    MATERIAL("material"),
    GRADE_COLOR("grade_color");

    companion object {
        fun getByValue(value: String) = values().find { it.value == value } ?: VULCAN
    }
}