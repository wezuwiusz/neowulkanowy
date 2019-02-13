package io.github.wulkanowy.data.repositories.timetable

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.utils.friday
import io.github.wulkanowy.utils.monday
import io.reactivex.Single
import org.threeten.bp.LocalDate
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimetableRepository @Inject constructor(
    private val settings: InternetObservingSettings,
    private val local: TimetableLocal,
    private val remote: TimetableRemote
) {

    fun getTimetable(semester: Semester, startDate: LocalDate, endDate: LocalDate, forceRefresh: Boolean = false)
        : Single<List<Timetable>> {
        return Single.fromCallable { startDate.monday to endDate.friday }
            .flatMap { dates ->
                local.getTimetable(semester, dates.first, dates.second).filter { !forceRefresh }
                    .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                        .flatMap {
                            if (it) remote.getTimetable(semester, dates.first, dates.second)
                            else Single.error(UnknownHostException())
                        }.flatMap { newTimetable ->
                            local.getTimetable(semester, dates.first, dates.second)
                                .toSingle(emptyList())
                                .doOnSuccess { oldTimetable ->
                                    local.deleteTimetable(oldTimetable - newTimetable)
                                    local.saveTimetable(newTimetable - oldTimetable)
                                }
                        }.flatMap {
                            local.getTimetable(semester, dates.first, dates.second)
                                .toSingle(emptyList())
                        }).map { list -> list.filter { it.date in startDate..endDate } }
            }
    }
}
