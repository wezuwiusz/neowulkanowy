package io.github.wulkanowy.utils

import kotlin.math.round

fun Double.roundToDecimalPlaces(places: Int = 2): Double {
    return round(this * 10 * places) / (10 * places)
}
