package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.timetable.TimetableRepository
import io.github.wulkanowy.utils.sunday
import io.github.wulkanowy.utils.monday
import io.reactivex.Completable
import kotlinx.coroutines.rx2.rxCompletable
import org.threeten.bp.LocalDate.now
import javax.inject.Inject

class TimetableWork @Inject constructor(private val timetableRepository: TimetableRepository) : Work {

    override fun create(student: Student, semester: Semester): Completable {
        return rxCompletable { timetableRepository.getTimetable(student, semester, now().monday, now().sunday, true).waitForResult() }
    }
}
