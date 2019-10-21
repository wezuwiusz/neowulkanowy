package io.github.wulkanowy.data.repositories.teacher

import io.github.wulkanowy.data.db.dao.TeacherDao
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Teacher
import io.reactivex.Maybe
import javax.inject.Inject

class TeacherLocal @Inject constructor(private val teacherDb: TeacherDao) {

    fun saveTeachers(teachers: List<Teacher>) {
        teacherDb.insertAll(teachers)
    }

    fun deleteTeachers(teachers: List<Teacher>) {
        teacherDb.deleteAll(teachers)
    }

    fun getTeachers(semester: Semester): Maybe<List<Teacher>> {
        return teacherDb.loadAll(semester.studentId, semester.classId).filter { it.isNotEmpty() }
    }
}
