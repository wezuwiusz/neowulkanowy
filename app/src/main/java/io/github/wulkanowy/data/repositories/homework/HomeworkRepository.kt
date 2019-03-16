package io.github.wulkanowy.data.repositories.homework

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.utils.friday
import io.github.wulkanowy.utils.monday
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

    fun getHomework(semester: Semester, start: LocalDate, end: LocalDate, forceRefresh: Boolean = false): Single<List<Homework>> {
        return Single.fromCallable { start.monday to end.friday }.flatMap { (monday, friday) ->
            local.getHomework(semester, monday, friday).filter { !forceRefresh }
                .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                    .flatMap {
                        if (it) remote.getHomework(semester, monday, friday)
                        else Single.error(UnknownHostException())
                    }.flatMap { newGrades ->
                        local.getHomework(semester, monday, friday).toSingle(emptyList())
                            .doOnSuccess { oldGrades ->
                                local.deleteHomework(oldGrades - newGrades)
                                local.saveHomework(newGrades - oldGrades)
                            }
                    }.flatMap { local.getHomework(semester, monday, friday).toSingle(emptyList()) })
        }
    }
}
