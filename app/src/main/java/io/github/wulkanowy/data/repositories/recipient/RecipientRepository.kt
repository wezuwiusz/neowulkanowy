package io.github.wulkanowy.data.repositories.recipient

import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.uniqueSubtract
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipientRepository @Inject constructor(
    private val local: RecipientLocal,
    private val remote: RecipientRemote
) {

    suspend fun getRecipients(student: Student, role: Int, unit: ReportingUnit, forceRefresh: Boolean = false): List<Recipient> {
        return local.getRecipients(student, role, unit).filter { !forceRefresh }.ifEmpty {
            val new = remote.getRecipients(student, role, unit)
            val old = local.getRecipients(student, role, unit)

            local.deleteRecipients(old.uniqueSubtract(new))
            local.saveRecipients(new.uniqueSubtract(old))

            local.getRecipients(student, role, unit)
        }
    }

    suspend fun getMessageRecipients(student: Student, message: Message): List<Recipient> {
        return remote.getMessageRecipients(student, message)
    }
}
