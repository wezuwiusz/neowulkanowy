package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.TeacherRepository
import io.github.wulkanowy.utils.waitForResult
import javax.inject.Inject

class TeacherWork @Inject constructor(private val teacherRepository: TeacherRepository) : Work {

    override suspend fun doWork(student: Student, semester: Semester) {
        teacherRepository.getTeachers(student, semester, true).waitForResult()
    }
}
