package io.github.wulkanowy.data.repositories.teacher

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.Teacher
import io.github.wulkanowy.utils.uniqueSubtract
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeacherRepository @Inject constructor(
    private val local: TeacherLocal,
    private val remote: TeacherRemote
) {

    suspend fun getTeachers(student: Student, semester: Semester, forceRefresh: Boolean = false): List<Teacher> {
        return local.getTeachers(semester).filter { !forceRefresh }.ifEmpty {
            val new = remote.getTeachers(student, semester)
            val old = local.getTeachers(semester)

            local.deleteTeachers(old.uniqueSubtract(new))
            local.saveTeachers(new.uniqueSubtract(old))

            local.getTeachers(semester)
        }
    }
}
