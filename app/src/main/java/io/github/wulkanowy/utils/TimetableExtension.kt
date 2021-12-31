package io.github.wulkanowy.utils

import io.github.wulkanowy.data.db.entities.Timetable
import java.time.Duration
import java.time.Duration.between
import java.time.Instant
import java.time.Instant.now

fun Timetable.isShowTimeUntil(previousLessonEnd: Instant?) = when {
    !isStudentPlan -> false
    canceled -> false
    now().isAfter(start) -> false
    previousLessonEnd != null && now().isBefore(previousLessonEnd) -> false
    else -> between(now(), start) <= Duration.ofMinutes(60)
}

inline val Timetable.left: Duration?
    get() = when {
        canceled -> null
        !isStudentPlan -> null
        end >= now() && start <= now() -> between(now(), end)
        else -> null
    }

inline val Timetable.until: Duration
    get() = between(now(), start)

inline val Timetable.isJustFinished: Boolean
    get() = end.isBefore(now()) && end.plusSeconds(15).isAfter(now()) && !canceled
