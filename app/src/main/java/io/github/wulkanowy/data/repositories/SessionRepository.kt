package io.github.wulkanowy.data.repositories

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.local.SessionLocal
import io.github.wulkanowy.data.repositories.remote.SessionRemote
import io.reactivex.Completable
import io.reactivex.Single
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepository @Inject constructor(
    private val local: SessionLocal,
    private val remote: SessionRemote,
    private val settings: InternetObservingSettings
) {

    val isSessionSaved
        get() = local.isSessionSaved

    lateinit var cachedStudents: Single<List<Student>>
        private set

    fun getConnectedStudents(email: String, password: String, symbol: String, endpoint: String): Single<List<Student>> {
        cachedStudents = ReactiveNetwork.checkInternetConnectivity(settings)
            .flatMap { isConnected ->
                if (isConnected) remote.getConnectedStudents(email, password, symbol, endpoint)
                else Single.error<List<Student>>(UnknownHostException("No internet connection"))
            }.doOnSuccess { cachedStudents = Single.just(it) }
        return cachedStudents
    }

    fun saveStudent(student: Student): Completable {
        return remote.getSemesters(student)
            .flatMapCompletable { local.saveSemesters(it) }
            .concatWith(local.saveStudent(student))
    }

    fun getSemesters(forceRefresh: Boolean = false): Single<List<Semester>> {
        return local.getLastStudent()
            .flatMapSingle { student ->
                remote.initApi(student)
                local.getSemesters(student).filter { !forceRefresh }
                    .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings).flatMap {
                        if (it) remote.getCurrentSemester(student)
                        else Single.error(UnknownHostException())
                    }.flatMap { current ->
                        local.getSemesters(student).doOnSuccess { semesters ->
                            if (semesters.single { it.current }.semesterId != current.semesterId) {
                                local.saveSemesters(listOf(current)).andThen {
                                    local.setCurrentSemester(current.semesterId)
                                }
                            }
                        }
                    }.flatMap {
                        local.getSemesters(student)
                    })
            }
    }
}
