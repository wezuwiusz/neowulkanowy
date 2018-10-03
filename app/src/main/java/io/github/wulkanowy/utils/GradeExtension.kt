package io.github.wulkanowy.utils

import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.GradeSummary

fun List<Grade>.calcAverage(): Double {
    var counter = 0.0
    var denominator = 0.0

    forEach {
        counter += (it.value + it.modifier) * it.weightValue
        denominator += it.weightValue
    }
    return if (denominator != 0.0) counter / denominator else 0.0
}

@JvmName("calcSummaryAverage")
fun List<GradeSummary>.calcAverage(): Double {
    return asSequence().mapNotNull {
        if (it.finalGrade.matches("[0-6]".toRegex())) it.finalGrade.toDouble() else null
    }.average()
}

inline val Grade.valueColor: Int
    get() {
        return when (value) {
            6 -> R.color.grade_six
            5 -> R.color.grade_five
            4 -> R.color.grade_four
            3 -> R.color.grade_three
            2 -> R.color.grade_two
            1 -> R.color.grade_one
            else -> R.color.grade_default
        }

    }

inline val Grade.colorStringId: Int
    get() {
        return when (color) {
            "000000" -> R.string.all_black
            "F04C4C" -> R.string.all_red
            "20A4F7" -> R.string.all_blue
            "6ECD07" -> R.string.all_green
            else -> R.string.all_empty_color
        }
    }