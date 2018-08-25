package io.github.wulkanowy.data.repositories

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.local.StudentLocal
import io.github.wulkanowy.data.repositories.remote.StudentRemote
import io.reactivex.Completable
import io.reactivex.Single
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentRepository @Inject constructor(
        private val local: StudentLocal,
        private val remote: StudentRemote,
        private val settings: InternetObservingSettings) {

    lateinit var cachedStudents: Single<List<Student>>
        private set

    val isStudentLoggedIn: Boolean
        get() = local.isStudentLoggedIn

    fun getConnectedStudents(email: String, password: String, symbol: String): Single<List<Student>> {
        cachedStudents = ReactiveNetwork.checkInternetConnectivity(settings)
                .flatMap { isConnected ->
                    if (isConnected) remote.getConnectedStudents(email, password, symbol)
                    else Single.error<List<Student>>(UnknownHostException("No internet connection"))
                }.doOnSuccess { cachedStudents = Single.just(it) }
        return cachedStudents
    }

    fun save(student: Student): Completable = local.save(student)

    fun getCurrentStudent(): Single<Student> = local.getCurrentStudent()

    fun clearCache() {
        cachedStudents = Single.just(emptyList())
    }
}
