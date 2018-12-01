package io.github.wulkanowy.data.repositories.local

import android.content.Context
import io.github.wulkanowy.data.db.SharedPrefHelper
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
    private val sharedPref: SharedPrefHelper,
    private val context: Context
) {

    companion object {
        const val STUDENT_SAVED_KEY: String = "is_student_saved"
    }

    val isStudentSaved
        get() = sharedPref.getBoolean(STUDENT_SAVED_KEY, false)

    fun saveStudent(student: Student): Single<Long> {
        return Single.fromCallable { studentDb.insert(student.copy(password = encrypt(student.password, context))) }
            .doOnSuccess { sharedPref.putBoolean(STUDENT_SAVED_KEY, true) }
    }

    fun getStudents(decryptPass: Boolean): Maybe<List<Student>> {
        return studentDb.loadAll()
            .map { list -> list.map { it.apply { if (decryptPass) password = decrypt(password) } } }
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
        }.doOnComplete { sharedPref.putBoolean(STUDENT_SAVED_KEY, true) }
    }

    fun logoutStudent(student: Student): Completable {
        return Completable.fromCallable {
            studentDb.delete(student)
            if (student.isCurrent) sharedPref.putBoolean(STUDENT_SAVED_KEY, false)
        }
    }
}
