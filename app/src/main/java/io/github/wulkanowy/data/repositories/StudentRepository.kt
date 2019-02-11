package io.github.wulkanowy.data.repositories

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.ApiHelper
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.local.StudentLocal
import io.github.wulkanowy.data.repositories.remote.StudentRemote
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentRepository @Inject constructor(
    private val local: StudentLocal,
    private val remote: StudentRemote,
    private val settings: InternetObservingSettings,
    private val apiHelper: ApiHelper
) {

    val isStudentSaved
        get() = local.isStudentSaved

    fun getStudents(email: String, password: String, endpoint: String, symbol: String = ""): Single<List<Student>> {
        return ReactiveNetwork.checkInternetConnectivity(settings)
            .flatMap {
                apiHelper.initApi(email, password, symbol, endpoint)
                if (it) remote.getStudents(email, password, endpoint)
                else Single.error(UnknownHostException("No internet connection"))
            }
    }

    fun getSavedStudents(decryptPass: Boolean = true): Single<List<Student>> {
        return local.getStudents(decryptPass).toSingle(emptyList())
    }

    fun getCurrentStudent(decryptPass: Boolean = true): Single<Student> {
        return local.getCurrentStudent(decryptPass)
            .switchIfEmpty(Maybe.error(NoSuchElementException("No current student")))
            .toSingle()
    }

    fun saveStudent(student: Student): Single<Long> {
        return local.saveStudent(student)
    }

    fun switchStudent(student: Student): Completable {
        return local.setCurrentStudent(student)
    }

    fun logoutStudent(student: Student): Completable {
        return local.logoutStudent(student)
    }
}
