package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.RecipientDao
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.init
import io.github.wulkanowy.utils.uniqueSubtract
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipientRepository @Inject constructor(
    private val recipientDb: RecipientDao,
    private val sdk: Sdk
) {

    suspend fun refreshRecipients(student: Student, role: Int, unit: ReportingUnit) {
        val new = sdk.init(student).getRecipients(unit.realId, role).mapToEntities(student)
        val old = recipientDb.loadAll(student.studentId, role, unit.realId)

        recipientDb.deleteAll(old uniqueSubtract new)
        recipientDb.insertAll(new uniqueSubtract old)
    }

    suspend fun getRecipients(student: Student, role: Int, unit: ReportingUnit): List<Recipient> {
        return recipientDb.loadAll(student.studentId, role, unit.realId).ifEmpty {
            refreshRecipients(student, role, unit)

            recipientDb.loadAll(student.studentId, role, unit.realId)
        }
    }

    suspend fun getMessageRecipients(student: Student, message: Message): List<Recipient> {
        return sdk.init(student).getMessageRecipients(message.messageId, message.senderId).mapToEntities(student)
    }
}
