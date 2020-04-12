package io.github.wulkanowy.data.repositories.school

import io.github.wulkanowy.data.db.entities.School
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.init
import io.reactivex.Single
import javax.inject.Inject

class SchoolRemote @Inject constructor(private val sdk: Sdk) {

    fun getSchoolInfo(student: Student, semester: Semester): Single<School> {
        return sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
            .getSchool()
            .map {
                School(
                    studentId = semester.studentId,
                    classId = semester.classId,
                    name = it.name,
                    address = it.address,
                    contact = it.contact,
                    headmaster = it.headmaster,
                    pedagogue = it.pedagogue
                )
            }
    }
}
