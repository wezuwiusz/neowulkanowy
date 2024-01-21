package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.HomeworkRepository
import io.github.wulkanowy.data.waitForResult
import io.github.wulkanowy.services.sync.notifications.NewHomeworkNotification
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.nextOrSameSchoolDay
import io.github.wulkanowy.utils.sunday
import kotlinx.coroutines.flow.first
import java.time.LocalDate.now
import javax.inject.Inject

class HomeworkWork @Inject constructor(
    private val homeworkRepository: HomeworkRepository,
    private val newHomeworkNotification: NewHomeworkNotification,
) : Work {

    override suspend fun doWork(student: Student, semester: Semester, notify: Boolean) {
        val startDate = now().nextOrSameSchoolDay.monday
        val endDate = startDate.sunday

        homeworkRepository.getHomework(
            student = student,
            semester = semester,
            start = startDate,
            end = endDate,
            forceRefresh = true,
            notify = notify,
        ).waitForResult()

        homeworkRepository.getHomeworkFromDatabase(
            semester = semester,
            start = startDate,
            end = endDate
        )
            .first()
            .filter { !it.isNotified }.let {
                if (it.isNotEmpty()) newHomeworkNotification.notify(it, student)

                homeworkRepository.updateHomework(it.onEach { homework ->
                    homework.isNotified = true
                })
            }
    }
}
