package io.github.wulkanowy.data.repositories.school

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.School
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.reactivex.Maybe
import io.reactivex.Single
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SchoolRepository @Inject constructor(
    private val settings: InternetObservingSettings,
    private val local: SchoolLocal,
    private val remote: SchoolRemote
) {

    fun getSchoolInfo(student: Student, semester: Semester, forceRefresh: Boolean = false): Maybe<School> {
        return local.getSchool(semester).filter { !forceRefresh }
            .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                .flatMap {
                    if (it) remote.getSchoolInfo(student, semester)
                    else Single.error(UnknownHostException())
                }.flatMapMaybe { new ->
                    local.getSchool(semester)
                        .doOnSuccess { old ->
                            if (new != old) {
                                local.deleteSchool(old)
                                local.saveSchool(new)
                            }
                        }
                        .doOnComplete {
                            local.saveSchool(new)
                        }
                }.flatMap({ local.getSchool(semester) }, { Maybe.error(it) },
                    { local.getSchool(semester) })
            )
    }
}
