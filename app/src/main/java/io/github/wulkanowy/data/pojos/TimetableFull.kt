package io.github.wulkanowy.data.pojos

import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.data.db.entities.TimetableAdditional
import io.github.wulkanowy.data.db.entities.TimetableHeader

data class TimetableFull(
    val lessons: List<Timetable>,
    val additional: List<TimetableAdditional>,
    val headers: List<TimetableHeader>,
)
