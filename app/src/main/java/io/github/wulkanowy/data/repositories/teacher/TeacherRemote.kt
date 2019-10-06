package io.github.wulkanowy.data.repositories.teacher

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Teacher
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeacherRemote @Inject constructor(private val api: Api) {

    fun getTeachers(semester: Semester): Single<List<Teacher>> {
        return Single.just(api.apply { diaryId = semester.diaryId })
            .flatMap { it.getTeachers() }
            .map { teachers ->
                teachers.map {
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
}
