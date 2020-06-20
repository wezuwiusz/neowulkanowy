package io.github.wulkanowy.data.repositories.teacher

import io.github.wulkanowy.data.db.dao.TeacherDao
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Teacher
import javax.inject.Inject

class TeacherLocal @Inject constructor(private val teacherDb: TeacherDao) {

    suspend fun saveTeachers(teachers: List<Teacher>) {
        teacherDb.insertAll(teachers)
    }

    suspend fun deleteTeachers(teachers: List<Teacher>) {
        teacherDb.deleteAll(teachers)
    }

    suspend fun getTeachers(semester: Semester): List<Teacher> {
        return teacherDb.loadAll(semester.studentId, semester.classId)
    }
}
