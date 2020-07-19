package io.github.wulkanowy.data.repositories.student

import android.content.Context
import io.github.wulkanowy.data.db.dao.StudentDao
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.DispatchersProvider
import io.github.wulkanowy.utils.security.decrypt
import io.github.wulkanowy.utils.security.encrypt
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentLocal @Inject constructor(
    private val studentDb: StudentDao,
    private val dispatchers: DispatchersProvider,
    private val context: Context
) {

    suspend fun saveStudents(students: List<Student>) = withContext(dispatchers.backgroundThread) {
        studentDb.insertAll(students.map {
            if (Sdk.Mode.valueOf(it.loginMode) != Sdk.Mode.API) it.copy(password = encrypt(it.password, context))
            else it
        })
    }

    suspend fun getStudents(decryptPass: Boolean) = withContext(dispatchers.backgroundThread) {
        studentDb.loadAll().map {
            it.apply {
                if (decryptPass && Sdk.Mode.valueOf(loginMode) != Sdk.Mode.API) password = decrypt(password)
            }
        }
    }

    suspend fun getStudentById(id: Int) = withContext(dispatchers.backgroundThread) {
        studentDb.loadById(id)?.apply {
            if (Sdk.Mode.valueOf(loginMode) != Sdk.Mode.API) password = decrypt(password)
        }
    }

    suspend fun getCurrentStudent(decryptPass: Boolean) = withContext(dispatchers.backgroundThread) {
        studentDb.loadCurrent()?.apply {
            if (decryptPass && Sdk.Mode.valueOf(loginMode) != Sdk.Mode.API) password = decrypt(password)
        }
    }

    suspend fun setCurrentStudent(student: Student) = withContext(dispatchers.backgroundThread) {
        studentDb.run {
            resetCurrent()
            updateCurrent(student.id)
        }
    }

    suspend fun logoutStudent(student: Student) = withContext(dispatchers.backgroundThread) {
        studentDb.delete(student)
    }
}
