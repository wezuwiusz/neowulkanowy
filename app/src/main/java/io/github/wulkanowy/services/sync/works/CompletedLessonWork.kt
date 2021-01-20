package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.CompletedLessonsRepository
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.sunday
import io.github.wulkanowy.utils.waitForResult
import java.time.LocalDate.now
import javax.inject.Inject

class CompletedLessonWork @Inject constructor(
    private val completedLessonsRepository: CompletedLessonsRepository
) : Work {

    override suspend fun doWork(student: Student, semester: Semester) {
        completedLessonsRepository.getCompletedLessons(student, semester, now().monday, now().sunday, true).waitForResult()
    }
}
