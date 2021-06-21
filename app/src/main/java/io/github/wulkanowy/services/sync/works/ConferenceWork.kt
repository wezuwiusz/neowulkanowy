package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.ConferenceRepository
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.services.sync.notifications.NewConferenceNotification
import io.github.wulkanowy.utils.waitForResult
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ConferenceWork @Inject constructor(
    private val conferenceRepository: ConferenceRepository,
    private val preferencesRepository: PreferencesRepository,
    private val newConferenceNotification: NewConferenceNotification,
) : Work {

    override suspend fun doWork(student: Student, semester: Semester) {
        conferenceRepository.getConferences(
            student = student,
            semester = semester,
            forceRefresh = true,
            notify = preferencesRepository.isNotificationsEnable
        ).waitForResult()

        conferenceRepository.getConferenceFromDatabase(semester).first()
            .filter { !it.isNotified }.let {
                if (it.isNotEmpty()) newConferenceNotification.notify(it)

                conferenceRepository.updateConference(it.onEach { conference ->
                    conference.isNotified = true
                })
            }
    }
}
