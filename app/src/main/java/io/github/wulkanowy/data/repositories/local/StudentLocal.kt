package io.github.wulkanowy.data.repositories.local

import android.content.Context
import io.github.wulkanowy.data.db.SharedPrefHelper
import io.github.wulkanowy.data.db.dao.StudentDao
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.security.Scrambler.decrypt
import io.github.wulkanowy.utils.security.Scrambler.encrypt
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentLocal @Inject constructor(
        private val studentDb: StudentDao,
        private val sharedPref: SharedPrefHelper,
        private val context: Context) {

    companion object {
        const val CURRENT_USER_KEY: String = "current_user_id"
    }

    val isStudentLoggedIn: Boolean
        get() = sharedPref.getLong(CURRENT_USER_KEY, 0) != 0L

    fun save(student: Student): Completable {
        return Single.fromCallable { studentDb.insert(student.copy(password = encrypt(student.password, context))) }
                .map { sharedPref.putLong(CURRENT_USER_KEY, it) }
                .ignoreElement()
    }

    fun getCurrentStudent(): Single<Student> {
        return studentDb.load(sharedPref.getLong(CURRENT_USER_KEY, defaultValue = 0))
                .map { it.apply { password = decrypt(password) } }
    }
}
