package io.github.wulkanowy.data.repositories.semester

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.SdkHelper
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.getCurrentOrLast
import io.github.wulkanowy.utils.isCurrent
import io.github.wulkanowy.utils.uniqueSubtract
import io.reactivex.Maybe
import io.reactivex.Single
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SemesterRepository @Inject constructor(
    private val remote: SemesterRemote,
    private val local: SemesterLocal,
    private val settings: InternetObservingSettings,
    private val sdkHelper: SdkHelper
) {

    fun getSemesters(student: Student, forceRefresh: Boolean = false, refreshOnNoCurrent: Boolean = false): Single<List<Semester>> {
        return Maybe.just(sdkHelper.init(student))
            .flatMap {
                local.getSemesters(student).filter { !forceRefresh }.filter {
                    if (refreshOnNoCurrent) {
                        it.any { semester -> semester.isCurrent }
                    } else true
                }
            }
            .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                .flatMap {
                    if (it) remote.getSemesters(student)
                    else Single.error(UnknownHostException())
                }.flatMap { new ->
                    if (new.isEmpty()) throw IllegalArgumentException("Empty semester list!")

                    local.getSemesters(student).toSingle(emptyList()).doOnSuccess { old ->
                        local.deleteSemesters(old.uniqueSubtract(new))
                        local.saveSemesters(new.uniqueSubtract(old))
                    }
                }.flatMap { local.getSemesters(student).toSingle(emptyList()) })
    }

    fun getCurrentSemester(student: Student, forceRefresh: Boolean = false): Single<Semester> {
        return getSemesters(student, forceRefresh).map { it.getCurrentOrLast() }
    }
}
