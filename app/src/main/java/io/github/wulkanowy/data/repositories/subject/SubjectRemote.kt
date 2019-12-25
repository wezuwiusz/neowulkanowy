package io.github.wulkanowy.data.repositories.subject

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Subject
import io.github.wulkanowy.sdk.Sdk
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectRemote @Inject constructor(private val sdk: Sdk) {

    fun getSubjects(semester: Semester): Single<List<Subject>> {
        return sdk.switchDiary(semester.diaryId, semester.schoolYear).getSubjects()
            .map { subjects ->
                subjects.map {
                    Subject(
                        studentId = semester.studentId,
                        diaryId = semester.diaryId,
                        name = it.name,
                        realId = it.id
                    )
                }
            }
    }
}
