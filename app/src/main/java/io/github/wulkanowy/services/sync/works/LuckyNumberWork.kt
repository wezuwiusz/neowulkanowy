package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.LuckyNumberRepository
import io.github.wulkanowy.data.waitForResult
import io.github.wulkanowy.services.sync.notifications.NewLuckyNumberNotification
import javax.inject.Inject

class LuckyNumberWork @Inject constructor(
    private val luckyNumberRepository: LuckyNumberRepository,
    private val newLuckyNumberNotification: NewLuckyNumberNotification,
) : Work {

    override suspend fun doWork(student: Student, semester: Semester, notify: Boolean) {
        luckyNumberRepository.getLuckyNumber(
            student = student,
            forceRefresh = true,
            notify = notify,
        ).waitForResult()

        luckyNumberRepository.getNotNotifiedLuckyNumber(student)?.let {
            newLuckyNumberNotification.notify(it, student)
            luckyNumberRepository.updateLuckyNumber(it.apply { isNotified = true })
        }
    }
}
