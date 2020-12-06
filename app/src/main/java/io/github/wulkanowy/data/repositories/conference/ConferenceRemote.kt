package io.github.wulkanowy.data.repositories.conference

import io.github.wulkanowy.data.db.entities.Conference
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.init
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConferenceRemote @Inject constructor(private val sdk: Sdk) {

    suspend fun getConferences(student: Student, semester: Semester): List<Conference> {
        return sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
            .getConferences()
            .map {
                it.agenda
                Conference(
                    studentId = student.studentId,
                    diaryId = semester.diaryId,
                    agenda = it.agenda,
                    conferenceId = it.id,
                    date = it.date,
                    presentOnConference = it.presentOnConference,
                    subject = it.subject,
                    title = it.title
                )
            }
    }
}
