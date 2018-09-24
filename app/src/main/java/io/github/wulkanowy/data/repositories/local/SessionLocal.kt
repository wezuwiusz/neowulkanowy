package io.github.wulkanowy.data.repositories.local

import android.content.Context
import io.github.wulkanowy.data.db.SharedPrefHelper
import io.github.wulkanowy.data.db.dao.SemesterDao
import io.github.wulkanowy.data.db.dao.StudentDao
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.security.Scrambler.decrypt
import io.github.wulkanowy.utils.security.Scrambler.encrypt
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionLocal @Inject constructor(
        private val studentDb: StudentDao,
        private val semesterDb: SemesterDao,
        private val sharedPref: SharedPrefHelper,
        private val context: Context) {

    companion object {
        const val LAST_USER_KEY: String = "last_user_id"
    }

    val isSessionSaved
        get() = sharedPref.getLong(LAST_USER_KEY, defaultValue = 0L) != 0L

    fun saveStudent(student: Student): Completable {
        return Single.fromCallable { studentDb.insert(student.copy(password = encrypt(student.password, context))) }
                .map { sharedPref.putLong(LAST_USER_KEY, it) }
                .ignoreElement()
    }

    fun getLastStudent(): Maybe<Student> {
        return studentDb.load(sharedPref.getLong(LAST_USER_KEY, defaultValue = 0))
                .map { it.apply { password = decrypt(password) } }
    }

    fun saveSemesters(semesters: List<Semester>): Completable {
        return Single.fromCallable { semesterDb.insertAll(semesters) }.ignoreElement()
    }

    fun getSemesters(student: Student): Single<List<Semester>> {
        return semesterDb.getSemester(student.studentId)
    }
}
