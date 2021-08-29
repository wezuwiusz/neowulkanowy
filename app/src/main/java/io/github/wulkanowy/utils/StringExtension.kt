package io.github.wulkanowy.utils

inline fun String?.ifNullOrBlank(defaultValue: () -> String) =
    if (isNullOrBlank()) defaultValue() else this

fun String.capitalise() =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

fun String.decapitalise() = replaceFirstChar { it.lowercase() }
