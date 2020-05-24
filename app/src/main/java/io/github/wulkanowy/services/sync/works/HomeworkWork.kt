package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.homework.HomeworkRepository
import io.github.wulkanowy.utils.sunday
import io.github.wulkanowy.utils.monday
import io.reactivex.Completable
import org.threeten.bp.LocalDate.now
import javax.inject.Inject

class HomeworkWork @Inject constructor(private val homeworkRepository: HomeworkRepository) : Work {

    override fun create(student: Student, semester: Semester): Completable {
        return homeworkRepository.getHomework(student, semester, now().monday, now().sunday, true).ignoreElement()
    }
}
