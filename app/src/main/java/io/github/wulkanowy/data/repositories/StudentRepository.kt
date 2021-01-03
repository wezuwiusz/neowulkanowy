package io.github.wulkanowy.data.repositories

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.data.db.dao.SemesterDao
import io.github.wulkanowy.data.db.dao.StudentDao
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.data.exceptions.NoCurrentStudentException
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.sdk.Sdk
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
    private val sdk: Sdk
) {

    suspend fun isStudentSaved(): Boolean = getSavedStudents(false).isNotEmpty()

    suspend fun isCurrentStudentSet(): Boolean = studentDb.loadCurrent()?.isCurrent ?: false

    suspend fun getStudentsApi(pin: String, symbol: String, token: String): List<StudentWithSemesters> {
        return sdk.getStudentsFromMobileApi(token, pin, symbol, "").mapToEntities()
    }

    suspend fun getStudentsScrapper(email: String, password: String, scrapperBaseUrl: String, symbol: String): List<StudentWithSemesters> {
        return sdk.getStudentsFromScrapper(email, password, scrapperBaseUrl, symbol).mapToEntities(password)
    }

    suspend fun getStudentsHybrid(email: String, password: String, scrapperBaseUrl: String, symbol: String): List<StudentWithSemesters> {
        return sdk.getStudentsHybrid(email, password, scrapperBaseUrl, "", symbol).mapToEntities(password)
    }

    suspend fun getSavedStudents(decryptPass: Boolean = true) = withContext(dispatchers.backgroundThread) {
        studentDb.loadStudentsWithSemesters().map {
            it.apply {
                if (decryptPass && Sdk.Mode.valueOf(student.loginMode) != Sdk.Mode.API) student.password = decrypt(student.password)
            }
        }
    }

    suspend fun getStudentById(id: Int) = withContext(dispatchers.backgroundThread) {
        studentDb.loadById(id)?.apply {
            if (Sdk.Mode.valueOf(loginMode) != Sdk.Mode.API) password = decrypt(password)
        }
    } ?: throw NoCurrentStudentException()

    suspend fun getCurrentStudent(decryptPass: Boolean = true) = withContext(dispatchers.backgroundThread) {
        studentDb.loadCurrent()?.apply {
            if (decryptPass && Sdk.Mode.valueOf(loginMode) != Sdk.Mode.API) password = decrypt(password)
        }
    } ?: throw NoCurrentStudentException()

    suspend fun saveStudents(studentsWithSemesters: List<StudentWithSemesters>): List<Long> {
        semesterDb.insertSemesters(studentsWithSemesters.flatMap { it.semesters })

        return withContext(dispatchers.backgroundThread) {
            studentDb.insertAll(studentsWithSemesters.map { it.student }.map {
                if (Sdk.Mode.valueOf(it.loginMode) != Sdk.Mode.API) it.copy(password = encrypt(it.password, context))
                else it
            })
        }
    }

    suspend fun switchStudent(studentWithSemesters: StudentWithSemesters) {
        with(studentDb) {
            resetCurrent()
            updateCurrent(studentWithSemesters.student.id)
        }
    }

    suspend fun logoutStudent(student: Student) {
        studentDb.delete(student)
    }
}
