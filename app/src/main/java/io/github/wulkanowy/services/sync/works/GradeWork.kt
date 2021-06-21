package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.GradeRepository
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.services.sync.notifications.NewGradeNotification
import io.github.wulkanowy.utils.waitForResult
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GradeWork @Inject constructor(
    private val gradeRepository: GradeRepository,
    private val preferencesRepository: PreferencesRepository,
    private val newGradeNotification: NewGradeNotification,
) : Work {

    override suspend fun doWork(student: Student, semester: Semester) {
        gradeRepository.getGrades(
            student = student,
            semester = semester,
            forceRefresh = true,
            notify = preferencesRepository.isNotificationsEnable
        ).waitForResult()

        gradeRepository.getGradesFromDatabase(semester).first()
            .filter { !it.isNotified }.let {
                if (it.isNotEmpty()) newGradeNotification.notifyDetails(it)

                gradeRepository.updateGrades(it.onEach { grade -> grade.isNotified = true })
            }

        gradeRepository.getGradesPredictedFromDatabase(semester).first()
            .filter { !it.isPredictedGradeNotified }.let {
                if (it.isNotEmpty()) newGradeNotification.notifyPredicted(it)

                gradeRepository.updateGradesSummary(it.onEach { grade ->
                    grade.isPredictedGradeNotified = true
                })
            }

        gradeRepository.getGradesFinalFromDatabase(semester).first()
            .filter { !it.isFinalGradeNotified }.let {
                if (it.isNotEmpty()) newGradeNotification.notifyFinal(it)

                gradeRepository.updateGradesSummary(it.onEach { grade ->
                    grade.isFinalGradeNotified = true
                })
            }
    }
}
