package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.SchoolAnnouncementRepository
import io.github.wulkanowy.services.sync.notifications.NewSchoolAnnouncementNotification
import io.github.wulkanowy.utils.waitForResult
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SchoolAnnouncementWork @Inject constructor(
    private val schoolAnnouncementRepository: SchoolAnnouncementRepository,
    private val preferencesRepository: PreferencesRepository,
    private val newSchoolAnnouncementNotification: NewSchoolAnnouncementNotification,
) : Work {

    override suspend fun doWork(student: Student, semester: Semester) {
        schoolAnnouncementRepository.getSchoolAnnouncements(
            student = student,
            forceRefresh = true,
            notify = preferencesRepository.isNotificationsEnable
        ).waitForResult()


        schoolAnnouncementRepository.getSchoolAnnouncementFromDatabase(student).first()
            .filter { !it.isNotified }.let {
                if (it.isNotEmpty()) newSchoolAnnouncementNotification.notify(it)

                schoolAnnouncementRepository.updateSchoolAnnouncement(it.onEach { schoolAnnouncement ->
                    schoolAnnouncement.isNotified = true
                })
            }
    }
}
