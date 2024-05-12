package io.github.wulkanowy.domain.adminmessage

import io.github.wulkanowy.data.Resource
import io.github.wulkanowy.data.db.entities.AdminMessage
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.enums.MessageType
import io.github.wulkanowy.data.mapResourceData
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.WulkanowyRepository
import io.github.wulkanowy.utils.AppInfo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAppropriateAdminMessageUseCase @Inject constructor(
    private val wulkanowyRepository: WulkanowyRepository,
    private val preferencesRepository: PreferencesRepository,
    private val appInfo: AppInfo
) {

    operator fun invoke(student: Student, type: MessageType): Flow<Resource<AdminMessage?>> {
        return invoke(student.scrapperBaseUrl, type)
    }

    operator fun invoke(scrapperBaseUrl: String, type: MessageType): Flow<Resource<AdminMessage?>> {
        return wulkanowyRepository.getAdminMessages().mapResourceData { adminMessages ->
            adminMessages
                .asSequence()
                .filter { it.isNotDismissed() }
                .filter { it.isVersionMatch() }
                .filter { it.isRegisterHostMatch(scrapperBaseUrl) }
                .filter { it.isFlavorMatch() }
                .filter { it.isTypeMatch(type) }
                .maxByOrNull { it.id }
        }
    }

    private fun AdminMessage.isNotDismissed(): Boolean {
        return id !in preferencesRepository.dismissedAdminMessageIds
    }

    private fun AdminMessage.isRegisterHostMatch(scrapperBaseUrl: String): Boolean {
        return targetRegisterHost?.let {
            scrapperBaseUrl.contains(it, true)
        } ?: true
    }

    private fun AdminMessage.isFlavorMatch(): Boolean {
        return targetFlavor?.equals(appInfo.buildFlavor, true) ?: true
    }

    private fun AdminMessage.isVersionMatch(): Boolean {
        val isCorrectMaxVersion = versionMax?.let { it >= appInfo.versionCode } ?: true
        val isCorrectMinVersion = versionMin?.let { it <= appInfo.versionCode } ?: true

        return isCorrectMaxVersion && isCorrectMinVersion
    }

    private fun AdminMessage.isTypeMatch(messageType: MessageType): Boolean {
        if (messageType in types) return true
        if (MessageType.GENERAL_MESSAGE in types) return true

        return false
    }
}
