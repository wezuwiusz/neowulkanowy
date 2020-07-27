package io.github.wulkanowy.data.repositories.timetable

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.services.alarm.TimetableNotificationSchedulerHelper
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.networkBoundResource
import io.github.wulkanowy.utils.sunday
import io.github.wulkanowy.utils.uniqueSubtract
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimetableRepository @Inject constructor(
    private val local: TimetableLocal,
    private val remote: TimetableRemote,
    private val schedulerHelper: TimetableNotificationSchedulerHelper
) {

    fun getTimetable(student: Student, semester: Semester, start: LocalDate, end: LocalDate, forceRefresh: Boolean) = networkBoundResource(
        shouldFetch = { it.isEmpty() || forceRefresh },
        query = { local.getTimetable(semester, start.monday, end.sunday).map { schedulerHelper.scheduleNotifications(it, student); it } },
        fetch = { remote.getTimetable(student, semester, start.monday, end.sunday) },
        saveFetchResult = { old, new ->
            local.deleteTimetable(old.uniqueSubtract(new).also { schedulerHelper.cancelScheduled(it) })
            local.saveTimetable(new.uniqueSubtract(old).also { schedulerHelper.scheduleNotifications(it, student) }.map { item ->
                item.also { new ->
                    old.singleOrNull { new.start == it.start }?.let { old ->
                        return@map new.copy(
                            room = if (new.room.isEmpty()) old.room else new.room,
                            teacher = if (new.teacher.isEmpty() && !new.changes && !old.changes) old.teacher else new.teacher
                        )
                    }
                }
            })
        },
        filterResult = { it.filter { item -> item.date in start..end } }
    )
}
