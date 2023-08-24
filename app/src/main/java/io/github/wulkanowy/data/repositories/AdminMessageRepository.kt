package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.Resource
import io.github.wulkanowy.data.api.AdminMessageService
import io.github.wulkanowy.data.db.dao.AdminMessageDao
import io.github.wulkanowy.data.db.entities.AdminMessage
import io.github.wulkanowy.data.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminMessageRepository @Inject constructor(
    private val adminMessageService: AdminMessageService,
    private val adminMessageDao: AdminMessageDao,
) {

    private val saveFetchResultMutex = Mutex()

    fun getAdminMessages(): Flow<Resource<List<AdminMessage>>> =
        networkBoundResource(
            mutex = saveFetchResultMutex,
            isResultEmpty = { false },
            query = { adminMessageDao.loadAll() },
            fetch = { adminMessageService.getAdminMessages() },
            shouldFetch = { true },
            saveFetchResult = { oldItems, newItems ->
                adminMessageDao.removeOldAndSaveNew(oldItems, newItems)
            },
            showSavedOnLoading = false,
        )
}
