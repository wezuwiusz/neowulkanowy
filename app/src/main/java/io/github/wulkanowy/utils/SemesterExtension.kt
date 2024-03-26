package io.github.wulkanowy.utils

import io.github.wulkanowy.data.db.entities.Semester
import java.time.LocalDate
import java.time.LocalDate.now
import java.time.Month

fun Semester.isCurrent(now: LocalDate = now()): Boolean {
    val shiftedStart = if (start.month == Month.SEPTEMBER) {
        start.minusDays(3)
    } else start

    val shiftedEnd = if (end.month == Month.AUGUST || end.month == Month.SEPTEMBER) {
        end.minusDays(3)
    } else end

    return now in shiftedStart..shiftedEnd
}

fun List<Semester>.getCurrentOrLast(): Semester {
    if (isEmpty()) throw IllegalStateException("Empty semester list")

    // when there is only one current semester
    singleOrNull { it.isCurrent() }?.let { return it }

    // when there is more than one current semester - find one with higher id
    singleOrNull { semester -> semester.semesterId == maxByOrNull { it.semesterId }?.semesterId }?.let { return it }

    // when there is no active kindergarten semester - get one from last year
    singleOrNull { semester -> semester.schoolYear == maxByOrNull { it.schoolYear }?.schoolYear }?.let { return it }

    throw IllegalArgumentException("Duplicated last semester! Semesters: ${joinToString(separator = "\n")}")
}
