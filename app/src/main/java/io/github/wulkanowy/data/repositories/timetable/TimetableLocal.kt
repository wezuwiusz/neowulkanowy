package io.github.wulkanowy.data.repositories.timetable

import io.github.wulkanowy.data.db.dao.TimetableDao
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Timetable
import io.reactivex.Maybe
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimetableLocal @Inject constructor(private val timetableDb: TimetableDao) {

    fun getTimetable(semester: Semester, startDate: LocalDate, endDate: LocalDate): Maybe<List<Timetable>> {
        return timetableDb.loadAll(semester.diaryId, semester.studentId, startDate, endDate)
            .filter { !it.isEmpty() }
    }

    fun saveTimetable(timetables: List<Timetable>) {
        timetableDb.insertAll(timetables)
    }

    fun deleteTimetable(timetables: List<Timetable>) {
        timetableDb.deleteAll(timetables)
    }
}
