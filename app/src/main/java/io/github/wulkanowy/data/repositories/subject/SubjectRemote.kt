package io.github.wulkanowy.data.repositories.subject

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.Subject
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.init
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectRemote @Inject constructor(private val sdk: Sdk) {

    suspend fun getSubjects(student: Student, semester: Semester): List<Subject> {
        return sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
            .getSubjects()
            .map {
                Subject(
                    studentId = semester.studentId,
                    diaryId = semester.diaryId,
                    name = it.name,
                    realId = it.id
                )
            }
    }
}
