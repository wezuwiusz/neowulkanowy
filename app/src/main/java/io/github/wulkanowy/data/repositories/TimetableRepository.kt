package io.github.wulkanowy.data.repositories

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.data.repositories.local.TimetableLocal
import io.github.wulkanowy.data.repositories.remote.TimetableRemote
import io.github.wulkanowy.utils.weekFirstDayAlwaysCurrent
import io.reactivex.Single
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.TemporalAdjusters
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimetableRepository @Inject constructor(
        private val settings: InternetObservingSettings,
        private val local: TimetableLocal,
        private val remote: TimetableRemote
) {

    fun getTimetable(semester: Semester, startDate: LocalDate, endDate: LocalDate, forceRefresh: Boolean = false): Single<List<Timetable>> {
        val start = startDate.weekFirstDayAlwaysCurrent
        val end = endDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY))

        return local.getLessons(semester, start, end).filter { !forceRefresh }
                .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings).flatMap {
                    if (it) remote.getLessons(semester, start, end)
                    else Single.error(UnknownHostException())
                }.flatMap { newLessons ->
                    local.getLessons(semester, start, end).toSingle(emptyList()).map { lessons ->
                        local.deleteLessons(lessons - newLessons)
                        local.saveLessons(newLessons - lessons)
                        newLessons
                    }
                }).map { list ->
                    list.asSequence().filter {
                        it.date in startDate..endDate
                    }.toList()
                }
    }
}
