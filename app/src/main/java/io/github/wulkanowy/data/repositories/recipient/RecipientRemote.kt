package io.github.wulkanowy.data.repositories.recipient

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipientRemote @Inject constructor(private val api: Api) {

    fun getRecipients(role: Int, unit: ReportingUnit): Single<List<Recipient>> {
        return api.getRecipients(role, unit.realId)
            .map { recipients ->
                recipients.map {
                    Recipient(
                        studentId = api.studentId,
                        name = it.name,
                        realName = it.name,
                        realId = it.id,
                        hash = it.hash,
                        loginId = it.loginId,
                        role = it.role,
                        unitId = it.reportingUnitId ?: 0
                    )
                }
            }
    }
}
