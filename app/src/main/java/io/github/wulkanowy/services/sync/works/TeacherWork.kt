package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.teacher.TeacherRepository
import io.reactivex.Completable
import javax.inject.Inject

class TeacherWork @Inject constructor(private val teacherRepository: TeacherRepository) : Work {

    override fun create(student: Student, semester: Semester): Completable {
        return teacherRepository.getTeachers(semester, true).ignoreElement()
    }
}
