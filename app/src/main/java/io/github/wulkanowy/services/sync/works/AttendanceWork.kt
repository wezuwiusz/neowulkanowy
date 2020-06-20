package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.attendance.AttendanceRepository
import io.github.wulkanowy.utils.sunday
import io.github.wulkanowy.utils.monday
import io.reactivex.Completable
import kotlinx.coroutines.rx2.rxCompletable
import org.threeten.bp.LocalDate.now
import javax.inject.Inject

class AttendanceWork @Inject constructor(private val attendanceRepository: AttendanceRepository) : Work {

    override fun create(student: Student, semester: Semester): Completable {
        return rxCompletable { attendanceRepository.getAttendance(student, semester, now().monday, now().sunday, true) }
    }
}
