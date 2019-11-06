package io.github.wulkanowy.data.repositories.school

import io.github.wulkanowy.data.db.dao.SchoolDao
import io.github.wulkanowy.data.db.entities.School
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Maybe
import javax.inject.Inject

class SchoolLocal @Inject constructor(private val schoolDb: SchoolDao) {

    fun saveSchool(school: School) {
        schoolDb.insertAll(listOf(school))
    }

    fun deleteSchool(school: School) {
        schoolDb.deleteAll(listOf(school))
    }

    fun getSchool(semester: Semester): Maybe<School> {
        return schoolDb.load(semester.studentId, semester.classId)
    }
}
