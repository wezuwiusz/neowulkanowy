package io.github.wulkanowy.ui.modules.message.tab

import io.github.wulkanowy.data.db.entities.Message

sealed class MessageTabDataItem(val viewType: MessageItemViewType) {

    data class MessageItem(
        val message: Message,
        val isSelected: Boolean,
        val isActionMode: Boolean
    ) : MessageTabDataItem(MessageItemViewType.MESSAGE)

    data class FilterHeader(
        val selectedMailbox: String?,
        val onlyUnread: Boolean?,
        val onlyWithAttachments: Boolean,
        val isEnabled: Boolean
    ) : MessageTabDataItem(MessageItemViewType.FILTERS)
}

enum class MessageItemViewType { FILTERS, MESSAGE }
