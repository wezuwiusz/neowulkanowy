package io.github.wulkanowy.data.repositories.timetable

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.data.db.entities.TimetableAdditional
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.init
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimetableRemote @Inject constructor(private val sdk: Sdk) {

    suspend fun getTimetable(student: Student, semester: Semester, startDate: LocalDate, endDate: LocalDate): Pair<List<Timetable>, List<TimetableAdditional>> {
        val (normal, additional) = sdk.init(student)
            .switchDiary(semester.diaryId, semester.schoolYear)
            .getTimetable(startDate, endDate)

        return normal.map {
            Timetable(
                studentId = semester.studentId,
                diaryId = semester.diaryId,
                number = it.number,
                start = it.start,
                end = it.end,
                date = it.date,
                subject = it.subject,
                subjectOld = it.subjectOld,
                group = it.group,
                room = it.room,
                roomOld = it.roomOld,
                teacher = it.teacher,
                teacherOld = it.teacherOld,
                info = it.info,
                isStudentPlan = it.studentPlan,
                changes = it.changes,
                canceled = it.canceled
            )
        } to additional.map {
            TimetableAdditional(
                studentId = semester.studentId,
                diaryId = semester.diaryId,
                subject = it.subject,
                date = it.date,
                start = it.start,
                end = it.end
            )
        }
    }
}
