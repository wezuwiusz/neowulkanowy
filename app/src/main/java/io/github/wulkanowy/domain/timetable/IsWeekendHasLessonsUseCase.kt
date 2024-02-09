package io.github.wulkanowy.domain.timetable

import io.github.wulkanowy.data.db.entities.Timetable
import java.time.DayOfWeek
import javax.inject.Inject

class IsWeekendHasLessonsUseCase @Inject constructor() {

    operator fun invoke(
        lessons: List<Timetable>,
    ): Boolean = lessons.any {
        it.date.dayOfWeek in listOf(
            DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY,
        )
    }
}
