package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.homework.HomeworkRepository
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.sunday
import io.reactivex.Completable
import kotlinx.coroutines.rx2.rxCompletable
import java.time.LocalDate.now
import javax.inject.Inject

class HomeworkWork @Inject constructor(private val homeworkRepository: HomeworkRepository) : Work {

    override fun create(student: Student, semester: Semester): Completable {
        return rxCompletable { homeworkRepository.getHomework(student, semester, now().monday, now().sunday, true).waitForResult() }
    }
}
