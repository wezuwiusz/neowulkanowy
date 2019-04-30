package io.github.wulkanowy.data.repositories.attendance

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Attendance
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
class AttendanceRepository @Inject constructor(
    private val settings: InternetObservingSettings,
    private val local: AttendanceLocal,
    private val remote: AttendanceRemote
) {

    fun getAttendance(semester: Semester, startDate: LocalDate, endDate: LocalDate, forceRefresh: Boolean)
        : Single<List<Attendance>> {
        return Single.fromCallable { startDate.monday to endDate.friday }
            .flatMap { dates ->
                local.getAttendance(semester, dates.first, dates.second).filter { !forceRefresh }
                    .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings).flatMap {
                        if (it) remote.getAttendance(semester, dates.first, dates.second)
                        else Single.error(UnknownHostException())
                    }.flatMap { newAttendance ->
                        local.getAttendance(semester, dates.first, dates.second)
                            .toSingle(emptyList())
                            .doOnSuccess { oldAttendance ->
                                local.deleteAttendance(oldAttendance.uniqueSubtract(newAttendance))
                                local.saveAttendance(newAttendance.uniqueSubtract(oldAttendance))
                            }
                    }.flatMap {
                        local.getAttendance(semester, dates.first, dates.second)
                            .toSingle(emptyList())
                    }).map { list -> list.filter { it.date in startDate..endDate } }
            }
    }
}
