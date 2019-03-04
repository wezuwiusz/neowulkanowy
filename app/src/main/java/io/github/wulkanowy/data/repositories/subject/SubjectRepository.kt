package io.github.wulkanowy.data.repositories.subject

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Subject
import io.reactivex.Single
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectRepository @Inject constructor(
    private val settings: InternetObservingSettings,
    private val local: SubjectLocal,
    private val remote: SubjectRemote
) {

    fun getSubjects(semester: Semester, forceRefresh: Boolean = false): Single<List<Subject>> {
        return local.getSubjects(semester).filter { !forceRefresh }
            .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                .flatMap {
                    if (it) remote.getSubjects(semester)
                    else Single.error(UnknownHostException())
                }.flatMap { new ->
                    local.getSubjects(semester)
                        .toSingle(emptyList())
                        .doOnSuccess { old ->
                            local.deleteSubjects(old - new)
                            local.saveSubjects(new - old)
                        }
                }.flatMap {
                    local.getSubjects(semester).toSingle(emptyList())
                })
    }
}
