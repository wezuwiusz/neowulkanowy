package io.github.wulkanowy.data.repositories.student

import android.content.Context
import io.github.wulkanowy.data.db.dao.StudentDao
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.security.decrypt
import io.github.wulkanowy.utils.security.encrypt
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentLocal @Inject constructor(
    private val studentDb: StudentDao,
    private val context: Context
) {

    fun saveStudent(student: Student): Single<Long> {
        return Single.fromCallable { studentDb.insert(student.copy(password = encrypt(student.password, context))) }
    }

    fun getStudents(decryptPass: Boolean): Maybe<List<Student>> {
        return studentDb.loadAll()
            .map { list -> list.map { it.apply { if (decryptPass) password = decrypt(password) } } }
            .filter { !it.isEmpty() }
    }

    fun getCurrentStudent(decryptPass: Boolean): Maybe<Student> {
        return studentDb.loadCurrent().map { it.apply { if (decryptPass) password = decrypt(password) } }
    }

    fun setCurrentStudent(student: Student): Completable {
        return Completable.fromCallable {
            studentDb.run {
                resetCurrent()
                updateCurrent(student.studentId)
            }
        }
    }

    fun logoutStudent(student: Student): Completable {
        return Completable.fromCallable { studentDb.delete(student) }
    }
}
