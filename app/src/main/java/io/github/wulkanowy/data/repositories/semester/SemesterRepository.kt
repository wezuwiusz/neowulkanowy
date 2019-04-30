package io.github.wulkanowy.data.repositories.semester

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.ApiHelper
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.uniqueSubtract
import io.reactivex.Maybe
import io.reactivex.Single
import timber.log.Timber
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SemesterRepository @Inject constructor(
    private val remote: SemesterRemote,
    private val local: SemesterLocal,
    private val settings: InternetObservingSettings,
    private val apiHelper: ApiHelper
) {

    fun getSemesters(student: Student, forceRefresh: Boolean = false): Single<List<Semester>> {
        return Maybe.just(apiHelper.initApi(student))
            .flatMap { local.getSemesters(student).filter { !forceRefresh } }
            .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                .flatMap {
                    if (it) remote.getSemesters(student) else Single.error(UnknownHostException())
                }.flatMap { new ->
                    val currentSemesters = new.filter { it.isCurrent }
                    if (currentSemesters.size == 1) {
                        local.getSemesters(student).toSingle(emptyList())
                            .doOnSuccess { old ->
                                local.deleteSemesters(old.uniqueSubtract(new))
                                local.saveSemesters(new.uniqueSubtract(old))
                            }
                    } else {
                        Timber.i("Current semesters list:\n${currentSemesters.joinToString(separator = "\n")}")
                        throw IllegalArgumentException("Current semester can be only one.")
                    }
                }.flatMap { local.getSemesters(student).toSingle(emptyList()) })
    }

    fun getCurrentSemester(student: Student, forceRefresh: Boolean = false): Single<Semester> {
        return getSemesters(student, forceRefresh).map { item -> item.single { it.isCurrent } }
    }
}
