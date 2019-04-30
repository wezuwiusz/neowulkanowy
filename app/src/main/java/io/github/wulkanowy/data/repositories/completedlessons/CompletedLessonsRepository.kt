package io.github.wulkanowy.data.repositories.completedlessons

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.CompletedLesson
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.utils.friday
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.uniqueSubtract
import io.reactivex.Single
import org.threeten.bp.LocalDate
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompletedLessonsRepository @Inject constructor(
    private val settings: InternetObservingSettings,
    private val local: CompletedLessonsLocal,
    private val remote: CompletedLessonsRemote
) {

    fun getCompletedLessons(semester: Semester, startDate: LocalDate, endDate: LocalDate, forceRefresh: Boolean = false): Single<List<CompletedLesson>> {
        return Single.fromCallable { startDate.monday to endDate.friday }
            .flatMap { dates ->
                local.getCompletedLessons(semester, dates.first, dates.second).filter { !forceRefresh }
                    .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                        .flatMap {
                            if (it) remote.getCompletedLessons(semester, dates.first, dates.second)
                            else Single.error(UnknownHostException())
                        }.flatMap { new ->
                            local.getCompletedLessons(semester, dates.first, dates.second)
                                .toSingle(emptyList())
                                .doOnSuccess { old ->
                                    local.deleteCompleteLessons(old.uniqueSubtract(new))
                                    local.saveCompletedLessons(new.uniqueSubtract(old))
                                }
                        }.flatMap {
                            local.getCompletedLessons(semester, dates.first, dates.second)
                                .toSingle(emptyList())
                        }).map { list -> list.filter { it.date in startDate..endDate } }
            }
    }
}
