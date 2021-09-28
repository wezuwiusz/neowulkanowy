package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.RecipientDao
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.AutoRefreshHelper
import io.github.wulkanowy.utils.getRefreshKey
import io.github.wulkanowy.utils.init
import io.github.wulkanowy.utils.uniqueSubtract
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipientRepository @Inject constructor(
    private val recipientDb: RecipientDao,
    private val sdk: Sdk,
    private val refreshHelper: AutoRefreshHelper,
) {

    private val cacheKey = "recipient"

    suspend fun refreshRecipients(student: Student, unit: ReportingUnit, role: Int) {
        val new = sdk.init(student).getRecipients(unit.unitId, role).mapToEntities(unit.studentId)
        val old = recipientDb.loadAll(unit.studentId, unit.unitId, role)

        recipientDb.deleteAll(old uniqueSubtract new)
        recipientDb.insertAll(new uniqueSubtract old)

        refreshHelper.updateLastRefreshTimestamp(getRefreshKey(cacheKey, student))
    }

    suspend fun getRecipients(student: Student, unit: ReportingUnit, role: Int): List<Recipient> {
        val cached = recipientDb.loadAll(unit.studentId, unit.unitId, role)

        val isExpired = refreshHelper.shouldBeRefreshed(getRefreshKey(cacheKey, student))
        return if (cached.isEmpty() || isExpired) {
            refreshRecipients(student, unit, role)
            recipientDb.loadAll(unit.studentId, unit.unitId, role)
        } else cached
    }

    suspend fun getMessageRecipients(student: Student, message: Message): List<Recipient> {
        return sdk.init(student).getMessageRecipients(message.messageId, message.senderId)
            .mapToEntities(student.studentId)
    }
}
