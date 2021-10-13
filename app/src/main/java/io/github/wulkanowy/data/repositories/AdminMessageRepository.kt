package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.api.AdminMessageService
import io.github.wulkanowy.data.db.dao.AdminMessageDao
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.AutoRefreshHelper
import io.github.wulkanowy.utils.getRefreshKey
import io.github.wulkanowy.utils.networkBoundResource
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminMessageRepository @Inject constructor(
    private val adminMessageService: AdminMessageService,
    private val adminMessageDao: AdminMessageDao,
    private val appInfo: AppInfo,
    private val refreshHelper: AutoRefreshHelper,
) {
    private val saveFetchResultMutex = Mutex()

    private val cacheKey = "admin_messages"

    suspend fun getAdminMessages(student: Student, forceRefresh: Boolean) = networkBoundResource(
        mutex = saveFetchResultMutex,
        query = { adminMessageDao.loadAll() },
        fetch = { adminMessageService.getAdminMessages() },
        shouldFetch = {
            refreshHelper.shouldBeRefreshed(getRefreshKey(cacheKey, student)) || forceRefresh
        },
        saveFetchResult = { oldItems, newItems ->
            adminMessageDao.removeOldAndSaveNew(oldItems, newItems)
            refreshHelper.updateLastRefreshTimestamp(cacheKey)
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
