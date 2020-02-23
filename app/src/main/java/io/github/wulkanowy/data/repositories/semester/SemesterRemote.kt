package io.github.wulkanowy.data.repositories.semester

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.Sdk
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SemesterRemote @Inject constructor(private val sdk: Sdk) {

    fun getSemesters(student: Student): Single<List<Semester>> {
        return sdk.getSemesters().map { semesters ->
            semesters.map {
                Semester(
                    studentId = student.studentId,
                    diaryId = it.diaryId,
                    diaryName = it.diaryName,
                    schoolYear = it.schoolYear,
                    semesterId = it.semesterId,
                    semesterName = it.semesterNumber,
                    start = it.start,
                    end = it.end,
                    classId = it.classId,
                    unitId = it.unitId
                )
            }
        }
    }
}
