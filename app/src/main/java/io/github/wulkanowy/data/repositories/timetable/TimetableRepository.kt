package io.github.wulkanowy.data.repositories.timetable

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.utils.friday
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.uniqueSubtract
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

    fun getTimetable(semester: Semester, start: LocalDate, end: LocalDate, forceRefresh: Boolean = false): Single<List<Timetable>> {
        return Single.fromCallable { start.monday to end.friday }.flatMap { (monday, friday) ->
            local.getTimetable(semester, monday, friday).filter { !forceRefresh }
                .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings).flatMap {
                    if (it) remote.getTimetable(semester, monday, friday)
                    else Single.error(UnknownHostException())
                }.flatMap { new ->
                    local.getTimetable(semester, monday, friday)
                        .toSingle(emptyList())
                        .doOnSuccess { old ->
                            local.deleteTimetable(old.uniqueSubtract(new))
                            local.saveTimetable(new.uniqueSubtract(old).map { item ->
                                item.also { new ->
                                    old.singleOrNull { new.start == it.start }?.let { old ->
                                        return@map new.copy(
                                            room = if (new.room.isEmpty()) old.room else new.room,
                                            teacher = if (new.teacher.isEmpty() && !new.changes) old.teacher else new.teacher
                                        )
                                    }
                                }
                            })
                        }
                }.flatMap {
                    local.getTimetable(semester, monday, friday).toSingle(emptyList())
                }).map { list -> list.filter { it.date in start..end } }
        }
    }
}
