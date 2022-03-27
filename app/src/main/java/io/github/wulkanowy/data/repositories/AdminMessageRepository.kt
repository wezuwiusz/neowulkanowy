package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.api.AdminMessageService
import io.github.wulkanowy.data.db.dao.AdminMessageDao
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.networkBoundResource
import io.github.wulkanowy.utils.AppInfo
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminMessageRepository @Inject constructor(
    private val adminMessageService: AdminMessageService,
    private val adminMessageDao: AdminMessageDao,
    private val appInfo: AppInfo
) {
    private val saveFetchResultMutex = Mutex()

    suspend fun getAdminMessages(student: Student) = networkBoundResource(
        mutex = saveFetchResultMutex,
        isResultEmpty = { it == null },
        query = { adminMessageDao.loadAll() },
        fetch = { adminMessageService.getAdminMessages() },
        shouldFetch = { true },
        saveFetchResult = { oldItems, newItems ->
            adminMessageDao.removeOldAndSaveNew(oldItems, newItems)
        },
        showSavedOnLoading = false,
        mapResult = { adminMessages ->
            adminMessages.filter { adminMessage ->
                val isCorrectRegister = adminMessage.targetRegisterHost?.let {
                    student.scrapperBaseUrl.contains(it, true)
                } ?: true
                val isCorrectFlavor =
                    adminMessage.targetFlavor?.equals(appInfo.buildFlavor, true) ?: true
                val isCorrectMaxVersion =
                    adminMessage.versionMax?.let { it >= appInfo.versionCode } ?: true
                val isCorrectMinVersion =
                    adminMessage.versionMin?.let { it <= appInfo.versionCode } ?: true

                isCorrectRegister && isCorrectFlavor && isCorrectMaxVersion && isCorrectMinVersion
            }.maxByOrNull { it.id }
        }
    )
}
