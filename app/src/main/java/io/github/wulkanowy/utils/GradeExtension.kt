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
fun List<GradeSummary>.calcAverage() = asSequence()
    .mapNotNull {
        if (it.finalGrade.matches("[0-6]".toRegex())) {
            it.finalGrade.toDouble()
        } else null
    }
    .average()
    .let { if (it.isNaN()) 0.0 else it }

fun Grade.getBackgroundColor(theme: String) = when (theme) {
    "grade_color" -> getGradeColor()
    "material" -> when (value.toInt()) {
        6 -> R.color.grade_material_six
        5 -> R.color.grade_material_five
        4 -> R.color.grade_material_four
        3 -> R.color.grade_material_three
        2 -> R.color.grade_material_two
        1 -> R.color.grade_material_one
        else -> R.color.grade_material_default
    }
    else -> when (value.toInt()) {
        6 -> R.color.grade_vulcan_six
        5 -> R.color.grade_vulcan_five
        4 -> R.color.grade_vulcan_four
        3 -> R.color.grade_vulcan_three
        2 -> R.color.grade_vulcan_two
        1 -> R.color.grade_vulcan_one
        else -> R.color.grade_vulcan_default
    }
}

fun Grade.getGradeColor() = when (color) {
    "000000" -> R.color.grade_black
    "F04C4C" -> R.color.grade_red
    "20A4F7" -> R.color.grade_blue
    "6ECD07" -> R.color.grade_green
    "B16CF1" -> R.color.grade_purple
    else -> R.color.grade_material_default
}

inline val Grade.colorStringId: Int
    get() = when (color) {
        "000000" -> R.string.all_black
        "F04C4C" -> R.string.all_red
        "20A4F7" -> R.string.all_blue
        "6ECD07" -> R.string.all_green
        "B16CF1" -> R.string.all_purple
        else -> R.string.all_empty_color
    }

fun Grade.changeModifier(plusModifier: Double, minusModifier: Double) = when {
    modifier > 0 -> copy(modifier = plusModifier)
    modifier < 0 -> copy(modifier = -minusModifier)
    else -> this
}
