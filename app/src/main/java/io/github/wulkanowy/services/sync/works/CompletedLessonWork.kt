package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.completedlessons.CompletedLessonsRepository
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.sunday
import io.reactivex.Completable
import kotlinx.coroutines.rx2.rxCompletable
import org.threeten.bp.LocalDate.now
import javax.inject.Inject

class CompletedLessonWork @Inject constructor(
    private val completedLessonsRepository: CompletedLessonsRepository
) : Work {

    override fun create(student: Student, semester: Semester): Completable {
        return rxCompletable { completedLessonsRepository.getCompletedLessons(student, semester, now().monday, now().sunday, true).waitForResult() }
    }
}
