package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.TeacherDao
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.init
import io.github.wulkanowy.utils.networkBoundResource
import io.github.wulkanowy.utils.uniqueSubtract
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeacherRepository @Inject constructor(
    private val teacherDb: TeacherDao,
    private val sdk: Sdk
) {

    fun getTeachers(student: Student, semester: Semester, forceRefresh: Boolean) = networkBoundResource(
        shouldFetch = { it.isEmpty() || forceRefresh },
        query = { teacherDb.loadAll(semester.studentId, semester.classId) },
        fetch = {
            sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
                .getTeachers(semester.semesterId)
                .mapToEntities(semester)
        },
        saveFetchResult = { old, new ->
            teacherDb.deleteAll(old uniqueSubtract new)
            teacherDb.insertAll(new uniqueSubtract old)
        }
    )
}
