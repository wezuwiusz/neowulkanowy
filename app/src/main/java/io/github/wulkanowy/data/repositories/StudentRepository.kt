package io.github.wulkanowy.data.repositories

import android.content.Context
import androidx.room.withTransaction
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.dao.SemesterDao
import io.github.wulkanowy.data.db.dao.StudentDao
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.StudentName
import io.github.wulkanowy.data.db.entities.StudentNickAndAvatar
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.data.exceptions.NoCurrentStudentException
import io.github.wulkanowy.data.mappers.mapToPojo
import io.github.wulkanowy.data.pojos.RegisterUser
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.DispatchersProvider
import io.github.wulkanowy.utils.init
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
    private val appDatabase: AppDatabase
) {

    suspend fun isCurrentStudentSet() = studentDb.loadCurrent()?.isCurrent ?: false

    suspend fun getStudentsApi(
        pin: String,
        symbol: String,
        token: String
    ): RegisterUser = sdk
        .getStudentsFromHebe(token, pin, symbol, "")
        .mapToPojo(null)

    suspend fun getUserSubjectsFromScrapper(
        email: String,
        password: String,
        scrapperBaseUrl: String,
        domainSuffix: String,
        symbol: String
    ): RegisterUser = sdk
        .getUserSubjectsFromScrapper(email, password, scrapperBaseUrl, domainSuffix, symbol)
        .mapToPojo(password)

    suspend fun getStudentsHybrid(
        email: String,
        password: String,
        scrapperBaseUrl: String,
        symbol: String
    ): RegisterUser = sdk
        .getStudentsHybrid(email, password, scrapperBaseUrl, "", symbol)
        .mapToPojo(password)

    suspend fun getSavedStudents(decryptPass: Boolean = true) =
        studentDb.loadStudentsWithSemesters()
            .map {
                it.apply {
                    if (decryptPass && Sdk.Mode.valueOf(student.loginMode) != Sdk.Mode.HEBE) {
                        student.password = withContext(dispatchers.io) {
                            decrypt(student.password)
                        }
                    }
                }
            }

    suspend fun getSavedStudentById(id: Long, decryptPass: Boolean = true) =
        studentDb.loadStudentWithSemestersById(id)?.apply {
            if (decryptPass && Sdk.Mode.valueOf(student.loginMode) != Sdk.Mode.HEBE) {
                student.password = withContext(dispatchers.io) {
                    decrypt(student.password)
                }
            }
        }

    suspend fun getStudentById(id: Long, decryptPass: Boolean = true): Student {
        val student = studentDb.loadById(id) ?: throw NoCurrentStudentException()

        if (decryptPass && Sdk.Mode.valueOf(student.loginMode) != Sdk.Mode.HEBE) {
            student.password = withContext(dispatchers.io) {
                decrypt(student.password)
            }
        }
        return student
    }

    suspend fun getCurrentStudent(decryptPass: Boolean = true): Student {
        val student = studentDb.loadCurrent() ?: throw NoCurrentStudentException()

        if (decryptPass && Sdk.Mode.valueOf(student.loginMode) != Sdk.Mode.HEBE) {
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
                    if (Sdk.Mode.valueOf(it.loginMode) != Sdk.Mode.HEBE) {
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

    suspend fun isOneUniqueStudent() = getSavedStudents(false)
        .distinctBy { it.student.studentName }.size == 1

    suspend fun authorizePermission(student: Student, semester: Semester, pesel: String) =
        sdk.init(student)
            .switchDiary(semester.diaryId, semester.kindergartenDiaryId, semester.schoolYear)
            .authorizePermission(pesel)

    suspend fun refreshStudentName(student: Student, semester: Semester) {
        val newCurrentApiStudent = sdk.init(student)
            .switchDiary(semester.diaryId, semester.kindergartenDiaryId, semester.schoolYear)
            .getCurrentStudent() ?: return

        val studentName = StudentName(
            studentName = "${newCurrentApiStudent.studentName} ${newCurrentApiStudent.studentSurname}"
        ).apply { id = student.id }

        studentDb.update(studentName)
    }
}
