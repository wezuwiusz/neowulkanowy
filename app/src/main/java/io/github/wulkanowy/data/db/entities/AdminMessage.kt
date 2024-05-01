package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.wulkanowy.data.enums.MessageType
import io.github.wulkanowy.data.serializers.SafeMessageTypeEnumListSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "AdminMessages")
data class AdminMessage(

    @PrimaryKey
    val id: Int,

    val title: String,

    val content: String,

    @ColumnInfo(name = "version_name")
    val versionMin: Int? = null,

    @ColumnInfo(name = "version_max")
    val versionMax: Int? = null,

    @ColumnInfo(name = "target_register_host")
    val targetRegisterHost: String? = null,

    @ColumnInfo(name = "target_flavor")
    val targetFlavor: String? = null,

    @ColumnInfo(name = "destination_url")
    val destinationUrl: String? = null,

    val priority: String,

    @SerialName("messageTypes")
    @Serializable(with = SafeMessageTypeEnumListSerializer::class)
    @ColumnInfo(name = "types", defaultValue = "[]")
    val types: List<MessageType> = emptyList(),

    @ColumnInfo(name = "is_ok_visible", defaultValue = "0")
    val isOkVisible: Boolean = false,

    @ColumnInfo(name = "is_x_visible", defaultValue = "0")
    val isXVisible: Boolean = false
)
