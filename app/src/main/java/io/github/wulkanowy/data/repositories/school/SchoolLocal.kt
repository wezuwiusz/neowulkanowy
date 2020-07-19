package io.github.wulkanowy.data.repositories.school

import io.github.wulkanowy.data.db.dao.SchoolDao
import io.github.wulkanowy.data.db.entities.School
import io.github.wulkanowy.data.db.entities.Semester
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SchoolLocal @Inject constructor(private val schoolDb: SchoolDao) {

    suspend fun saveSchool(school: School) {
        schoolDb.insertAll(listOf(school))
    }

    suspend fun deleteSchool(school: School) {
        schoolDb.deleteAll(listOf(school))
    }

    fun getSchool(semester: Semester): Flow<School?> {
        return schoolDb.load(semester.studentId, semester.classId)
    }
}
