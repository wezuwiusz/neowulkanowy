package io.github.wulkanowy.data.repositories

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.repositories.local.AttendanceLocal
import io.github.wulkanowy.data.repositories.remote.AttendanceRemote
import io.github.wulkanowy.utils.extension.getWeekFirstDayAlwaysCurrent
import io.reactivex.Single
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.TemporalAdjusters
import java.net.UnknownHostException
import javax.inject.Inject

class AttendanceRepository @Inject constructor(
        private val settings: InternetObservingSettings,
        private val local: AttendanceLocal,
        private val remote: AttendanceRemote
) {

    fun getAttendance(semester: Semester, startDate: LocalDate, endDate: LocalDate, forceRefresh: Boolean = false): Single<List<Attendance>> {
        val start = startDate.getWeekFirstDayAlwaysCurrent()
        val end = endDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY))

        return local.getAttendance(semester, start, end).filter { !forceRefresh }
                .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings).flatMap {
                    if (it) remote.getAttendance(semester, start, end)
                    else Single.error(UnknownHostException())
                }.flatMap { newLessons ->
                    local.getAttendance(semester, start, end).toSingle(emptyList()).map { grades ->
                        local.deleteAttendance(grades - newLessons)
                        local.saveAttendance(newLessons - grades)
                        newLessons
                    }
                }).map { list ->
                    list.asSequence().filter {
                        it.date in startDate..endDate
                    }.toList()
                }
    }
}
