package io.github.wulkanowy.data.repositories.timetable

import io.github.wulkanowy.data.db.dao.TimetableDao
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Timetable
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimetableLocal @Inject constructor(private val timetableDb: TimetableDao) {

    suspend fun saveTimetable(timetables: List<Timetable>) {
        timetableDb.insertAll(timetables)
    }

    suspend fun deleteTimetable(timetables: List<Timetable>) {
        timetableDb.deleteAll(timetables)
    }

    fun getTimetable(semester: Semester, startDate: LocalDate, endDate: LocalDate): Flow<List<Timetable>> {
        return timetableDb.loadAll(semester.diaryId, semester.studentId, startDate, endDate)
    }
}
