package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.recipient.RecipientRepository
import io.github.wulkanowy.data.repositories.reportingunit.ReportingUnitRepository
import io.reactivex.Completable
import javax.inject.Inject

class RecipientWork @Inject constructor(
    private val reportingUnitRepository: ReportingUnitRepository,
    private val recipientRepository: RecipientRepository
) : Work {

    override fun create(student: Student, semester: Semester): Completable {
        return reportingUnitRepository.getReportingUnits(student)
            .flatMapCompletable { units ->
                Completable.mergeDelayError(units.map {
                    recipientRepository.getRecipients(student, 2, it).ignoreElement()
                })
            }
    }
}

