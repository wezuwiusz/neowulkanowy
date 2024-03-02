package io.github.wulkanowy.domain.timetable

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.repositories.TimetableRepository
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.sunday
import java.time.LocalDate
import javax.inject.Inject

class IsStudentHasLessonsOnWeekendUseCase @Inject constructor(
    private val timetableRepository: TimetableRepository,
    private val isWeekendHasLessonsUseCase: IsWeekendHasLessonsUseCase,
) {

    suspend operator fun invoke(
        semester: Semester,
        currentDate: LocalDate = LocalDate.now(),
    ): Boolean {
        val lessons = timetableRepository.getTimetableFromDatabase(
            semester = semester,
            start = currentDate.monday,
            end = currentDate.sunday,
        )
        return isWeekendHasLessonsUseCase(lessons)
    }
}
