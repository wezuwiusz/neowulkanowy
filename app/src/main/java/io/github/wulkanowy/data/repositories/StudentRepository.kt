package io.github.wulkanowy.data.repositories

import android.content.Context
import androidx.room.withTransaction
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.dao.SemesterDao
import io.github.wulkanowy.data.db.dao.StudentDao
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.StudentNickAndAvatar
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.data.exceptions.NoCurrentStudentException
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.DispatchersProvider
import io.github.wulkanowy.utils.security.decrypt
import io.github.wulkanowy.utils.security.encrypt
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatchers: DispatchersProvider,
    private val studentDb: StudentDao,
    private val semesterDb: SemesterDao,
    private val sdk: Sdk,
    private val appInfo: AppInfo,
    private val appDatabase: AppDatabase
) {

    suspend fun isStudentSaved() = getSavedStudents(false).isNotEmpty()

    suspend fun isCurrentStudentSet() = studentDb.loadCurrent()?.isCurrent ?: false

    suspend fun getStudentsApi(
        pin: String,
        symbol: String,
        token: String
    ): List<StudentWithSemesters> =
        sdk.getStudentsFromMobileApi(token, pin, symbol, "")
            .mapToEntities(colors = appInfo.defaultColorsForAvatar)

    suspend fun getStudentsScrapper(
        email: String,
        password: String,
        scrapperBaseUrl: String,
        symbol: String
    ): List<StudentWithSemesters> =
        sdk.getStudentsFromScrapper(email, password, scrapperBaseUrl, symbol)
            .mapToEntities(password, appInfo.defaultColorsForAvatar)

    suspend fun getStudentsHybrid(
        email: String,
        password: String,
        scrapperBaseUrl: String,
        symbol: String
    ): List<StudentWithSemesters> =
        sdk.getStudentsHybrid(email, password, scrapperBaseUrl, "", symbol)
            .mapToEntities(password, appInfo.defaultColorsForAvatar)

    suspend fun getSavedStudents(decryptPass: Boolean = true) =
        studentDb.loadStudentsWithSemesters()
            .map {
                it.apply {
                    if (decryptPass && Sdk.Mode.valueOf(student.loginMode) != Sdk.Mode.API) {
                        student.password = withContext(dispatchers.io) {
                            decrypt(student.password)
                        }
                    }
                }
            }

    suspend fun getStudentById(id: Long, decryptPass: Boolean = true): Student {
        val student = studentDb.loadById(id) ?: throw NoCurrentStudentException()

        if (decryptPass && Sdk.Mode.valueOf(student.loginMode) != Sdk.Mode.API) {
            student.password = withContext(dispatchers.io) {
                decrypt(student.password)
            }
        }
        return student
    }

    suspend fun getCurrentStudent(decryptPass: Boolean = true): Student {
        val student = studentDb.loadCurrent() ?: throw NoCurrentStudentException()

        if (decryptPass && Sdk.Mode.valueOf(student.loginMode) != Sdk.Mode.API) {
            student.password = withContext(dispatchers.io) {
                decrypt(student.password)
            }
        }
        return student
    }

    suspend fun saveStudents(studentsWithSemesters: List<StudentWithSemesters>) {
        val semesters = studentsWithSemesters.flatMap { it.semesters }
        val students = studentsWithSemesters.map { it.student }
            .map {
                it.apply {
                    if (Sdk.Mode.valueOf(it.loginMode) != Sdk.Mode.API) {
                        password = withContext(dispatchers.io) {
                            encrypt(password, context)
                        }
                    }
                }
            }
            .mapIndexed { index, student ->
                if (index == 0) {
                    student.copy(isCurrent = true).apply { avatarColor = student.avatarColor }
                } else student
            }

        appDatabase.withTransaction {
            studentDb.resetCurrent()
            semesterDb.insertSemesters(semesters)
            studentDb.insertAll(students)
        }
    }

    suspend fun switchStudent(studentWithSemesters: StudentWithSemesters) {
        studentDb.switchCurrent(studentWithSemesters.student.id)
    }

    suspend fun logoutStudent(student: Student) = studentDb.delete(student)

    suspend fun updateStudentNickAndAvatar(studentNickAndAvatar: StudentNickAndAvatar) =
        studentDb.update(studentNickAndAvatar)
}
