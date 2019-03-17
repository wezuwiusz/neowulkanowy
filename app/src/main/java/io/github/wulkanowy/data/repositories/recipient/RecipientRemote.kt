package io.github.wulkanowy.data.repositories.recipient

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton
import io.github.wulkanowy.api.messages.Recipient as ApiRecipient

@Singleton
class RecipientRemote @Inject constructor(private val api: Api) {

    fun getRecipients(role: Int, unit: ReportingUnit): Single<List<Recipient>> {
        return api.getRecipients(unit.realId, role)
            .map { recipients ->
                recipients.map { it.toRecipient() }
            }
    }

    fun getMessageRecipients(message: Message): Single<List<Recipient>> {
        return api.getMessageRecipients(message.messageId, message.senderId)
            .map { recipients ->
                recipients.map { it.toRecipient() }
            }
    }

    private fun ApiRecipient.toRecipient(): Recipient {
        return Recipient(
            studentId = api.studentId,
            realId = id,
            realName = name,
            name = shortName,
            hash = hash,
            loginId = loginId,
            role = role,
            unitId = reportingUnitId ?: 0
        )
    }
}
