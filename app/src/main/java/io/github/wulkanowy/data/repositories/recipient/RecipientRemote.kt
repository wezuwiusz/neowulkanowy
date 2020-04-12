package io.github.wulkanowy.data.repositories.recipient

import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.init
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton
import io.github.wulkanowy.sdk.pojo.Recipient as SdkRecipient

@Singleton
class RecipientRemote @Inject constructor(private val sdk: Sdk) {

    fun getRecipients(student: Student, role: Int, unit: ReportingUnit): Single<List<Recipient>> {
        return sdk.init(student).getRecipients(unit.realId, role)
            .map { recipients ->
                recipients.map { it.toRecipient() }
            }
    }

    fun getMessageRecipients(student: Student, message: Message): Single<List<Recipient>> {
        return sdk.init(student).getMessageRecipients(message.messageId, message.senderId)
            .map { recipients ->
                recipients.map { it.toRecipient() }
            }
    }

    private fun SdkRecipient.toRecipient(): Recipient {
        return Recipient(
            studentId = sdk.studentId,
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
