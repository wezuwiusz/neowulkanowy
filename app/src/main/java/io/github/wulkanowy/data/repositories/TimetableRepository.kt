package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.TimetableAdditionalDao
import io.github.wulkanowy.data.db.dao.TimetableDao
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.data.db.entities.TimetableAdditional
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.services.alarm.TimetableNotificationSchedulerHelper
import io.github.wulkanowy.utils.AutoRefreshHelper
import io.github.wulkanowy.utils.getRefreshKey
import io.github.wulkanowy.utils.init
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.networkBoundResource
import io.github.wulkanowy.utils.sunday
import io.github.wulkanowy.utils.uniqueSubtract
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimetableRepository @Inject constructor(
    private val timetableDb: TimetableDao,
    private val timetableAdditionalDb: TimetableAdditionalDao,
    private val sdk: Sdk,
    private val schedulerHelper: TimetableNotificationSchedulerHelper,
    private val refreshHelper: AutoRefreshHelper,
) {

    private val cacheKey = "timetable"

    fun getTimetable(student: Student, semester: Semester, start: LocalDate, end: LocalDate, forceRefresh: Boolean, refreshAdditional: Boolean = false) = networkBoundResource(
        shouldFetch = { (timetable, additional) -> timetable.isEmpty() || (additional.isEmpty() && refreshAdditional) || forceRefresh || refreshHelper.isShouldBeRefreshed(getRefreshKey(cacheKey, semester, start, end)) },
        query = {
            timetableDb.loadAll(semester.diaryId, semester.studentId, start.monday, end.sunday)
                .map { schedulerHelper.scheduleNotifications(it, student); it }
                .combine(timetableAdditionalDb.loadAll(semester.diaryId, semester.studentId, start.monday, end.sunday)) { timetable, additional ->
                    timetable to additional
                }
        },
        fetch = {
            sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
                .getTimetable(start.monday, end.sunday)
                .let { (normal, additional) -> normal.mapToEntities(semester) to additional.mapToEntities(semester) }

        },
        saveFetchResult = { (oldTimetable, oldAdditional), (newTimetable, newAdditional) ->
            refreshTimetable(student, oldTimetable, newTimetable)
            refreshAdditional(oldAdditional, newAdditional)

            refreshHelper.updateLastRefreshTimestamp(getRefreshKey(cacheKey, semester, start, end))
        },
        filterResult = { (timetable, additional) ->
            timetable.filter { item ->
                item.date in start..end
            } to additional.filter { item ->
                item.date in start..end
            }
        }
    )

    private suspend fun refreshTimetable(student: Student, old: List<Timetable>, new: List<Timetable>) {
        timetableDb.deleteAll(old.uniqueSubtract(new).also { schedulerHelper.cancelScheduled(it) })
        timetableDb.insertAll(new.uniqueSubtract(old).also { schedulerHelper.scheduleNotifications(it, student) }.map { item ->
            item.also { new ->
                old.singleOrNull { new.start == it.start }?.let { old ->
                    return@map new.copy(
                        room = if (new.room.isEmpty()) old.room else new.room,
                        teacher = if (new.teacher.isEmpty() && !new.changes && !old.changes) old.teacher else new.teacher
                    )
                }
            }
        })
    }

    private suspend fun refreshAdditional(old: List<TimetableAdditional>, new: List<TimetableAdditional>) {
        timetableAdditionalDb.deleteAll(old.uniqueSubtract(new))
        timetableAdditionalDb.insertAll(new.uniqueSubtract(old))
    }
}
