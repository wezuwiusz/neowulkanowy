package io.github.wulkanowy.data.repositories.student

import android.content.Context
import io.github.wulkanowy.data.db.dao.StudentDao
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.security.decrypt
import io.github.wulkanowy.utils.security.encrypt
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentLocal @Inject constructor(
    private val studentDb: StudentDao,
    private val context: Context
) {

    suspend fun saveStudents(students: List<Student>): List<Long> {
        return studentDb.insertAll(students.map {
            if (Sdk.Mode.valueOf(it.loginMode) != Sdk.Mode.API) it.copy(password = encrypt(it.password, context))
            else it
        })
    }

    suspend fun getStudents(decryptPass: Boolean): List<Student> {
        return studentDb.loadAll().map {
            it.apply {
                if (decryptPass && Sdk.Mode.valueOf(loginMode) != Sdk.Mode.API) password = decrypt(password)
            }
        }
    }

    suspend fun getStudentById(id: Int): Student? {
        return studentDb.loadById(id)?.apply {
            if (Sdk.Mode.valueOf(loginMode) != Sdk.Mode.API) password = decrypt(password)
        }
    }

    suspend fun getCurrentStudent(decryptPass: Boolean): Student? {
        return studentDb.loadCurrent()?.apply {
            if (decryptPass && Sdk.Mode.valueOf(loginMode) != Sdk.Mode.API) password = decrypt(password)
        }
    }

    suspend fun setCurrentStudent(student: Student) {
        return studentDb.run {
            resetCurrent()
            updateCurrent(student.id)
        }
    }

    suspend fun logoutStudent(student: Student) {
        return studentDb.delete(student)
    }
}
