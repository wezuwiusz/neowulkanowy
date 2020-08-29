package io.github.wulkanowy.data.repositories.recipient

import io.github.wulkanowy.data.db.dao.RecipientDao
import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.github.wulkanowy.data.db.entities.Student
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipientLocal @Inject constructor(private val recipientDb: RecipientDao) {

    suspend fun getRecipients(student: Student, role: Int, unit: ReportingUnit): List<Recipient> {
        return recipientDb.load(student.studentId, role, unit.realId)
    }

    suspend fun saveRecipients(recipients: List<Recipient>): List<Long> {
        return recipientDb.insertAll(recipients)
    }

    suspend fun deleteRecipients(recipients: List<Recipient>) {
        recipientDb.deleteAll(recipients)
    }
}
