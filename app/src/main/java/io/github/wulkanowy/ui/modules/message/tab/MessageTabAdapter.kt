package io.github.wulkanowy.ui.modules.message.tab

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.ItemMessageBinding
import io.github.wulkanowy.databinding.ItemMessageChipsBinding
import io.github.wulkanowy.utils.toFormattedString
import javax.inject.Inject

class MessageTabAdapter @Inject constructor() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onItemClickListener: (MessageTabDataItem.MessageItem, position: Int) -> Unit = { _, _ -> }

    var onLongItemClickListener: (MessageTabDataItem.MessageItem) -> Unit = {}

    var onHeaderClickListener: (CompoundButton, Boolean) -> Unit = { _, _ -> }

    var onChangesDetectedListener = {}

    private var items = mutableListOf<MessageTabDataItem>()

    fun submitData(data: List<MessageTabDataItem>) {
        val originalMessagesSize = items.count { it.viewType == MessageItemViewType.MESSAGE }
        val newMessagesSize = data.count { it.viewType == MessageItemViewType.MESSAGE }

        if (originalMessagesSize != newMessagesSize) onChangesDetectedListener()

        val diffResult = DiffUtil.calculateDiff(MessageTabDiffUtil(items, data))
        items = data.toMutableList()

        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemViewType(position: Int) = items[position].viewType.ordinal

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (MessageItemViewType.values()[viewType]) {
            MessageItemViewType.MESSAGE -> ItemViewHolder(
                ItemMessageBinding.inflate(inflater, parent, false)
            )
            MessageItemViewType.FILTERS -> HeaderViewHolder(
                ItemMessageChipsBinding.inflate(inflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemViewHolder -> bindItemViewHolder(holder, position)
            is HeaderViewHolder -> bindHeaderViewHolder(holder, position)
        }
    }

    private fun bindHeaderViewHolder(holder: HeaderViewHolder, position: Int) {
        val item = items[position] as MessageTabDataItem.FilterHeader

        with(holder.binding) {
            if (item.onlyUnread == null) {
                chipUnread.isVisible = false
            } else {
                chipUnread.isVisible = true
                chipUnread.isChecked = item.onlyUnread
                chipUnread.setOnCheckedChangeListener(onHeaderClickListener)
            }
            chipUnread.isEnabled = item.isEnabled
            chipAttachments.isEnabled = item.isEnabled
            chipAttachments.isChecked = item.onlyWithAttachments
            chipAttachments.setOnCheckedChangeListener(onHeaderClickListener)
        }
    }

    private fun bindItemViewHolder(holder: ItemViewHolder, position: Int) {
        val item = (items[position] as MessageTabDataItem.MessageItem)
        val message = item.message

        with(holder.binding) {
            val style = if (message.unread) Typeface.BOLD else Typeface.NORMAL

            with(messageItemAuthor) {
                text = message.correspondents
                setTypeface(null, style)
            }
            messageItemSubject.run {
                text = message.subject.ifBlank { context.getString(R.string.message_no_subject) }
                setTypeface(null, style)
            }
            messageItemDate.run {
                text = message.date.toFormattedString()
                setTypeface(null, style)
            }
            messageItemAttachmentIcon.isVisible = message.hasAttachments

            root.setOnClickListener {
                holder.bindingAdapterPosition.let {
                    if (it != RecyclerView.NO_POSITION) {
                        onItemClickListener(item, it)
                    }
                }
            }

            root.setOnLongClickListener {
                onLongItemClickListener(item)
                return@setOnLongClickListener true
            }

            with(messageItemCheckbox) {
                isChecked = item.isSelected
                isVisible = item.isActionMode
            }
        }
    }

    class ItemViewHolder(val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root)

    class HeaderViewHolder(val binding: ItemMessageChipsBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class MessageTabDiffUtil(
        private val old: List<MessageTabDataItem>,
        private val new: List<MessageTabDataItem>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = old.size

        override fun getNewListSize(): Int = new.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = old[oldItemPosition]
            val newItem = new[newItemPosition]

            return if (oldItem is MessageTabDataItem.MessageItem && newItem is MessageTabDataItem.MessageItem) {
                oldItem.message.messageGlobalKey == newItem.message.messageGlobalKey
            } else {
                oldItem.viewType == newItem.viewType
            }
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            old[oldItemPosition] == new[newItemPosition]
    }
}
