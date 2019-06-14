package io.github.wulkanowy.utils

inline fun String?.ifNullOrBlank(defaultValue: () -> String) = if (this.isNullOrBlank()) defaultValue() else this
