package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.NotificationDao
import io.github.wulkanowy.data.db.entities.Notification
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val notificationDao: NotificationDao,
) {

    fun getNotifications(studentId: Long) = notificationDao.loadAll(studentId)

    suspend fun saveNotification(notification: Notification) =
        notificationDao.insertAll(listOf(notification))
}
