package io.github.wulkanowy.domain.timetable

import io.github.wulkanowy.data.dataOrNull
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.TimetableRepository
import io.github.wulkanowy.data.toFirstResult
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.sunday
import java.time.LocalDate
import javax.inject.Inject

class IsStudentHasLessonsOnWeekendUseCase @Inject constructor(
    private val timetableRepository: TimetableRepository,
    private val isWeekendHasLessonsUseCase: IsWeekendHasLessonsUseCase,
) {

    suspend operator fun invoke(
        student: Student,
        semester: Semester,
        currentDate: LocalDate = LocalDate.now(),
    ): Boolean {
        val lessons = timetableRepository.getTimetable(
            student = student,
            semester = semester,
            start = currentDate.monday,
            end = currentDate.sunday,
            forceRefresh = false,
            timetableType = TimetableRepository.TimetableType.NORMAL
        ).toFirstResult().dataOrNull?.lessons.orEmpty()
        return isWeekendHasLessonsUseCase(lessons)
    }
}
