package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.LuckyNumberRepository
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.services.sync.notifications.NewLuckyNumberNotification
import io.github.wulkanowy.utils.waitForResult
import javax.inject.Inject

class LuckyNumberWork @Inject constructor(
    private val luckyNumberRepository: LuckyNumberRepository,
    private val preferencesRepository: PreferencesRepository,
    private val newLuckyNumberNotification: NewLuckyNumberNotification,
) : Work {

    override suspend fun doWork(student: Student, semester: Semester) {
        luckyNumberRepository.getLuckyNumber(
            student = student,
            forceRefresh = true,
            notify = preferencesRepository.isNotificationsEnable
        ).waitForResult()

        luckyNumberRepository.getNotNotifiedLuckyNumber(student)?.let {
            newLuckyNumberNotification.notify(it)
            luckyNumberRepository.updateLuckyNumber(it.apply { isNotified = true })
        }
    }
}
