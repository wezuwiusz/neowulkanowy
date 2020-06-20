package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.teacher.TeacherRepository
import io.reactivex.Completable
import kotlinx.coroutines.rx2.rxCompletable
import javax.inject.Inject

class TeacherWork @Inject constructor(private val teacherRepository: TeacherRepository) : Work {

    override fun create(student: Student, semester: Semester): Completable {
        return rxCompletable { teacherRepository.getTeachers(student, semester, true) }
    }
}
