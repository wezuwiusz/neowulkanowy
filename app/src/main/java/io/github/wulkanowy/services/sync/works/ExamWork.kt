package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.ExamRepository
import io.github.wulkanowy.utils.waitForResult
import java.time.LocalDate.now
import javax.inject.Inject

class ExamWork @Inject constructor(private val examRepository: ExamRepository) : Work {

    override suspend fun doWork(student: Student, semester: Semester) {
        examRepository.getExams(student, semester, now(), now(), true).waitForResult()
    }
}
