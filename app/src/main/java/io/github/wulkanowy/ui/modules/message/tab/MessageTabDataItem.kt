package io.github.wulkanowy.ui.modules.message.tab

import io.github.wulkanowy.data.db.entities.Message

sealed class MessageTabDataItem {
    data class MessageItem(val message: Message) : MessageTabDataItem() {
        override val id = message.id
    }

    object Header : MessageTabDataItem() {
        override val id = Long.MIN_VALUE
    }

    abstract val id: Long
}
