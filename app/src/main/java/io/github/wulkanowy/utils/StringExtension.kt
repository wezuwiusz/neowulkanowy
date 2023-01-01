package io.github.wulkanowy.utils

import androidx.core.text.parseAsHtml
import org.apache.commons.text.StringEscapeUtils

inline fun String?.ifNullOrBlank(defaultValue: () -> String) =
    if (isNullOrBlank()) defaultValue() else this

fun String.capitalise() =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

fun String.parseUonetHtml() = this
    .let(StringEscapeUtils::unescapeHtml4)
    .replace("\n", "<br/>")
    .parseAsHtml()
