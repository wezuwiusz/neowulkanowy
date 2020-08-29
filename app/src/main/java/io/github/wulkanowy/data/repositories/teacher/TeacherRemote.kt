package io.github.wulkanowy.data.repositories.teacher

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.Teacher
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.init
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeacherRemote @Inject constructor(private val sdk: Sdk) {

    suspend fun getTeachers(student: Student, semester: Semester): List<Teacher> {
        return sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
            .getTeachers(semester.semesterId)
            .map {
                Teacher(
                    studentId = semester.studentId,
                    name = it.name,
                    subject = it.subject,
                    shortName = it.short,
                    classId = semester.classId
                )
            }
    }
}
