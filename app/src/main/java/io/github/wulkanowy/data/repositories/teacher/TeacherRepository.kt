package io.github.wulkanowy.data.repositories.teacher

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Teacher
import io.github.wulkanowy.utils.uniqueSubtract
import io.reactivex.Single
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeacherRepository @Inject constructor(
    private val settings: InternetObservingSettings,
    private val local: TeacherLocal,
    private val remote: TeacherRemote
) {

    fun getTeachers(semester: Semester, forceRefresh: Boolean = false): Single<List<Teacher>> {
        return local.getTeachers(semester).filter { !forceRefresh }
            .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                .flatMap {
                    if (it) remote.getTeachers(semester)
                    else Single.error(UnknownHostException())
                }.flatMap { new ->
                    local.getTeachers(semester).toSingle(emptyList())
                        .doOnSuccess { old ->
                            local.deleteTeachers(old.uniqueSubtract(new))
                            local.saveTeachers(new.uniqueSubtract(old))
                        }
                }.flatMap { local.getTeachers(semester).toSingle(emptyList()) })
    }
}
