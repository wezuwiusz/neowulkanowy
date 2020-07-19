package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.recipient.RecipientRepository
import io.github.wulkanowy.data.repositories.reportingunit.ReportingUnitRepository
import io.reactivex.Completable
import kotlinx.coroutines.rx2.rxCompletable
import kotlinx.coroutines.rx2.rxSingle
import javax.inject.Inject

class RecipientWork @Inject constructor(
    private val reportingUnitRepository: ReportingUnitRepository,
    private val recipientRepository: RecipientRepository
) : Work {

    override fun create(student: Student, semester: Semester): Completable {
        return rxSingle { reportingUnitRepository.refreshReportingUnits(student) }
            .flatMap { rxSingle { reportingUnitRepository.getReportingUnits(student) } }
            .flatMapCompletable { units ->
                Completable.mergeDelayError(units.map {
                    rxCompletable { recipientRepository.refreshRecipients(student, 2, it) }
                })
            }
    }
}

