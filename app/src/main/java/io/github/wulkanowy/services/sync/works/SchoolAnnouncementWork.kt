package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.SchoolAnnouncementRepository
import io.github.wulkanowy.utils.waitForResult
import javax.inject.Inject

class SchoolAnnouncementWork @Inject constructor(
    private val schoolAnnouncementRepository: SchoolAnnouncementRepository,
) : Work {

    override suspend fun doWork(student: Student, semester: Semester) {
        schoolAnnouncementRepository.getSchoolAnnouncements(student, true).waitForResult()
    }
}
