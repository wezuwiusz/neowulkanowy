package io.github.wulkanowy.data.repositories.school

import io.github.wulkanowy.data.db.entities.School
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.init
import javax.inject.Inject

class SchoolRemote @Inject constructor(private val sdk: Sdk) {

    suspend fun getSchoolInfo(student: Student, semester: Semester): School {
        return sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
            .getSchool()
            .let {
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
