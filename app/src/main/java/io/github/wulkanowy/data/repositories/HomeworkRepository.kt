package io.github.wulkanowy.data.repositories

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.repositories.local.HomeworkLocal
import io.github.wulkanowy.data.repositories.remote.HomeworkRemote
import io.reactivex.Single
import org.threeten.bp.LocalDate
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeworkRepository @Inject constructor(
    private val settings: InternetObservingSettings,
    private val local: HomeworkLocal,
    private val remote: HomeworkRemote
) {

    fun getHomework(semester: Semester, date: LocalDate, forceRefresh: Boolean = false): Single<List<Homework>> {
        return local.getHomework(semester, date).filter { !forceRefresh }
            .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                .flatMap {
                    if (it) remote.getHomework(semester, date)
                    else Single.error(UnknownHostException())
                }.flatMap { newGrades ->
                    local.getHomework(semester, date).toSingle(emptyList())
                        .doOnSuccess { oldGrades ->
                            local.deleteHomework(oldGrades - newGrades)
                            local.saveHomework(newGrades - oldGrades)
                        }
                }.flatMap { local.getHomework(semester, date).toSingle(emptyList()) })
    }
}
