package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.wulkanowy.services.sync.notifications.NotificationType
import io.github.wulkanowy.ui.modules.Destination
import java.time.Instant

@Entity(tableName = "Notifications")
data class Notification(

    @ColumnInfo(name = "student_id")
    val studentId: Long,

    val title: String,

    val content: String,

    val type: NotificationType,

    @ColumnInfo(defaultValue = "{\"type\":\"io.github.wulkanowy.ui.modules.Destination.Dashboard\"}")
    val destination: Destination,

    val date: Instant,

    val data: String? = null
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}