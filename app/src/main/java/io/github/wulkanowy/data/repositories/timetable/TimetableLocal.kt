package io.github.wulkanowy.data.repositories.timetable

import io.github.wulkanowy.data.db.dao.TimetableAdditionalDao
import io.github.wulkanowy.data.db.dao.TimetableDao
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.data.db.entities.TimetableAdditional
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimetableLocal @Inject constructor(
    private val timetableDb: TimetableDao,
    private val timetableAdditionalDb: TimetableAdditionalDao
) {

    suspend fun saveTimetable(timetables: List<Timetable>) {
        timetableDb.insertAll(timetables)
    }

    suspend fun saveTimetableAdditional(additional: List<TimetableAdditional>) {
        timetableAdditionalDb.insertAll(additional)
    }

    suspend fun deleteTimetable(timetables: List<Timetable>) {
        timetableDb.deleteAll(timetables)
    }

    suspend fun deleteTimetableAdditional(additional: List<TimetableAdditional>) {
        timetableAdditionalDb.deleteAll(additional)
    }

    fun getTimetable(semester: Semester, startDate: LocalDate, endDate: LocalDate): Flow<List<Timetable>> {
        return timetableDb.loadAll(semester.diaryId, semester.studentId, startDate, endDate)
    }

    fun getTimetableAdditional(semester: Semester, startDate: LocalDate, endDate: LocalDate): Flow<List<TimetableAdditional>> {
        return timetableAdditionalDb.loadAll(semester.diaryId, semester.studentId, startDate, endDate)
    }
}
