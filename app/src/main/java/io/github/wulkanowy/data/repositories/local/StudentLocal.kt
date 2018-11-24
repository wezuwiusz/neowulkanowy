package io.github.wulkanowy.data.repositories.local

import android.content.Context
import io.github.wulkanowy.data.db.SharedPrefHelper
import io.github.wulkanowy.data.db.dao.StudentDao
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.security.decrypt
import io.github.wulkanowy.utils.security.encrypt
import io.reactivex.Completable
import io.reactivex.Maybe
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

    fun saveStudent(student: Student): Completable {
        return Completable.fromCallable {
            studentDb.run {
                resetCurrent()
                studentDb.insert(student.copy(password = encrypt(student.password, context)))
            }
        }.doOnComplete { sharedPref.putBoolean(STUDENT_SAVED_KEY, true) }
    }

    fun getCurrentStudent(): Maybe<Student> {
        return studentDb.loadCurrent().map { it.apply { password = decrypt(password) } }
    }

    fun getStudents(): Maybe<List<Student>> {
        return studentDb.loadAll()
    }

    fun setCurrentStudent(student: Student): Completable {
        return Completable.fromCallable {
            studentDb.run {
                resetCurrent()
                update(student.apply { isCurrent = true })
            }
        }.doOnComplete { sharedPref.putBoolean(STUDENT_SAVED_KEY, true) }
    }

    fun logoutCurrentStudent(): Completable {
        return studentDb.loadCurrent().doOnSuccess {
            studentDb.delete(it)
            sharedPref.putBoolean(STUDENT_SAVED_KEY, false)
        }.ignoreElement()
    }
}
