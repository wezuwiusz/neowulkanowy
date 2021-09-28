package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.StudentInfoDao
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.mappers.mapToEntity
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.init
import io.github.wulkanowy.utils.networkBoundResource
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentInfoRepository @Inject constructor(
    private val studentInfoDao: StudentInfoDao,
    private val sdk: Sdk
) {

    private val saveFetchResultMutex = Mutex()

    fun getStudentInfo(
        student: Student,
        semester: Semester,
        forceRefresh: Boolean,
    ) = networkBoundResource(
        mutex = saveFetchResultMutex,
        shouldFetch = { it == null || forceRefresh },
        query = { studentInfoDao.loadStudentInfo(student.studentId) },
        fetch = {
            sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
                .getStudentInfo().mapToEntity(semester)
        },
        saveFetchResult = { old, new ->
            if (old != null && new != old) {
                with(studentInfoDao) {
                    deleteAll(listOf(old))
                    insertAll(listOf(new))
                }
            } else if (old == null) {
                studentInfoDao.insertAll(listOf(new))
            }
        }
    )
}
