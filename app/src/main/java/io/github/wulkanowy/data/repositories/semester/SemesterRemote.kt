package io.github.wulkanowy.data.repositories.semester

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SemesterRemote @Inject constructor(private val api: Api) {

    fun getSemesters(student: Student): Single<List<Semester>> {
        return api.getSemesters().map { semesters ->
            semesters.map { semester ->
                Semester(
                    studentId = student.studentId,
                    diaryId = semester.diaryId,
                    diaryName = semester.diaryName,
                    semesterId = semester.semesterId,
                    semesterName = semester.semesterNumber,
                    isCurrent = semester.current,
                    classId = semester.classId,
                    unitId = semester.unitId
                )
            }

        }
    }
}


