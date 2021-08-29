package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.TimetableAdditionalDao
import io.github.wulkanowy.data.db.dao.TimetableDao
import io.github.wulkanowy.data.db.dao.TimetableHeaderDao
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.data.db.entities.TimetableAdditional
import io.github.wulkanowy.data.db.entities.TimetableHeader
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.data.pojos.TimetableFull
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.services.alarm.TimetableNotificationSchedulerHelper
import io.github.wulkanowy.utils.AutoRefreshHelper
import io.github.wulkanowy.utils.getRefreshKey
import io.github.wulkanowy.utils.init
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.networkBoundResource
import io.github.wulkanowy.utils.sunday
import io.github.wulkanowy.utils.uniqueSubtract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.sync.Mutex
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimetableRepository @Inject constructor(
    private val timetableDb: TimetableDao,
    private val timetableAdditionalDb: TimetableAdditionalDao,
    private val timetableHeaderDb: TimetableHeaderDao,
    private val sdk: Sdk,
    private val schedulerHelper: TimetableNotificationSchedulerHelper,
    private val refreshHelper: AutoRefreshHelper,
) {

    private val saveFetchResultMutex = Mutex()

    private val cacheKey = "timetable"

    fun getTimetable(
        student: Student, semester: Semester, start: LocalDate, end: LocalDate,
        forceRefresh: Boolean, refreshAdditional: Boolean = false
    ) = networkBoundResource(
        mutex = saveFetchResultMutex,
        shouldFetch = { (timetable, additional, headers) ->
            val refreshKey = getRefreshKey(cacheKey, semester, start, end)
            val isShouldRefresh = refreshHelper.isShouldBeRefreshed(refreshKey)
            val isRefreshAdditional = additional.isEmpty() && refreshAdditional

            val isNoData = timetable.isEmpty() || isRefreshAdditional || headers.isEmpty()

            isNoData || forceRefresh || isShouldRefresh
        },
        query = { getFullTimetableFromDatabase(student, semester, start, end) },
        fetch = {
            val timetableFull = sdk.init(student)
                .switchDiary(semester.diaryId, semester.schoolYear)
                .getTimetableFull(start.monday, end.sunday)

            timetableFull.mapToEntities(semester)
        },
        saveFetchResult = { timetableOld, timetableNew ->
            refreshTimetable(student, timetableOld.lessons, timetableNew.lessons)
            refreshAdditional(timetableOld.additional, timetableNew.additional)
            refreshDayHeaders(timetableOld.headers, timetableNew.headers)

            refreshHelper.updateLastRefreshTimestamp(getRefreshKey(cacheKey, semester, start, end))
        },
        filterResult = { (timetable, additional, headers) ->
            TimetableFull(
                lessons = timetable.filter { it.date in start..end },
                additional = additional.filter { it.date in start..end },
                headers = headers.filter { it.date in start..end }
            )
        }
    )

    private fun getFullTimetableFromDatabase(
        student: Student, semester: Semester,
        start: LocalDate, end: LocalDate
    ): Flow<TimetableFull> {
        val timetableFlow = timetableDb.loadAll(
            diaryId = semester.diaryId,
            studentId = semester.studentId,
            from = start.monday,
            end = end.sunday
        )
        val headersFlow = timetableHeaderDb.loadAll(
            diaryId = semester.diaryId,
            studentId = semester.studentId,
            from = start.monday,
            end = end.sunday
        )
        val additionalFlow = timetableAdditionalDb.loadAll(
            diaryId = semester.diaryId,
            studentId = semester.studentId,
            from = start.monday,
            end = end.sunday
        )
        return combine(timetableFlow, headersFlow, additionalFlow) { lessons, headers, additional ->
            schedulerHelper.scheduleNotifications(lessons, student)

            TimetableFull(
                lessons = lessons,
                headers = headers,
                additional = additional
            )
        }
    }

    private suspend fun refreshTimetable(
        student: Student,
        lessonsOld: List<Timetable>, lessonsNew: List<Timetable>
    ) {
        val lessonsToRemove = lessonsOld uniqueSubtract lessonsNew
        val lessonsToAdd = (lessonsNew uniqueSubtract lessonsOld).map { new ->
            val matchingOld = lessonsOld.singleOrNull { new.start == it.start }
            if (matchingOld != null) {
                val useOldTeacher = new.teacher.isEmpty() && !new.changes && !matchingOld.changes
                new.copy(
                    room = if (new.room.isEmpty()) matchingOld.room else new.room,
                    teacher = if (useOldTeacher) matchingOld.teacher
                    else new.teacher
                )
            } else new
        }

        timetableDb.deleteAll(lessonsToRemove)
        timetableDb.insertAll(lessonsToAdd)

        schedulerHelper.cancelScheduled(lessonsToRemove, student)
        schedulerHelper.scheduleNotifications(lessonsToAdd, student)
    }

    private suspend fun refreshAdditional(
        old: List<TimetableAdditional>,
        new: List<TimetableAdditional>
    ) {
        timetableAdditionalDb.deleteAll(old uniqueSubtract new)
        timetableAdditionalDb.insertAll(new uniqueSubtract old)
    }

    private suspend fun refreshDayHeaders(old: List<TimetableHeader>, new: List<TimetableHeader>) {
        timetableHeaderDb.deleteAll(old uniqueSubtract new)
        timetableHeaderDb.insertAll(new uniqueSubtract old)
    }
}
