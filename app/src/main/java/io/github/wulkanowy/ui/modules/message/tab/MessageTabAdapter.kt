package io.github.wulkanowy.ui.modules.message.tab

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.ItemMessageBinding
import io.github.wulkanowy.databinding.ItemMessageChipsBinding
import io.github.wulkanowy.utils.getCompatColor
import io.github.wulkanowy.utils.getThemeAttrColor
import io.github.wulkanowy.utils.toFormattedString
import javax.inject.Inject

class MessageTabAdapter @Inject constructor() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var onItemClickListener: (MessageTabDataItem.MessageItem, position: Int) -> Unit

    lateinit var onLongItemClickListener: (MessageTabDataItem.MessageItem) -> Unit

    lateinit var onHeaderClickListener: (CompoundButton, Boolean) -> Unit

    lateinit var onMailboxClickListener: () -> Unit

    lateinit var onChangesDetectedListener: () -> Unit

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
            MessageItemViewType.FILTERS -> HeaderViewHolder(
                ItemMessageChipsBinding.inflate(inflater, parent, false)
            )
            MessageItemViewType.MESSAGE -> ItemViewHolder(
                ItemMessageBinding.inflate(inflater, parent, false)
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
            chipMailbox.text = item.selectedMailbox
                ?: root.context.getString(R.string.message_chip_all_mailboxes)
            chipMailbox.chipBackgroundColor = ColorStateList.valueOf(
                if (item.selectedMailbox == null) {
                    root.context.getCompatColor(R.color.mtrl_choice_chip_background_color)
                } else root.context.getThemeAttrColor(android.R.attr.colorPrimary, 64)
            )
            chipMailbox.setTextColor(
                if (item.selectedMailbox == null) {
                    root.context.getThemeAttrColor(android.R.attr.textColorPrimary)
                } else root.context.getThemeAttrColor(android.R.attr.colorPrimary)
            )
            chipMailbox.setOnClickListener { onMailboxClickListener() }

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
            val normalFont = Typeface.create("sans-serif", Typeface.NORMAL)
            val boldFont = Typeface.create("sans-serif-black", Typeface.NORMAL)

            val primaryColor = root.context.getThemeAttrColor(android.R.attr.textColorPrimary)
            val secondaryColor = root.context.getThemeAttrColor(android.R.attr.textColorSecondary)

            val currentFont = if (message.unread) boldFont else normalFont
            val currentTextColor = if (message.unread) primaryColor else secondaryColor

            with(messageItemAuthor) {
                text = message.correspondents
                setTextColor(currentTextColor)
                typeface = currentFont
            }
            with(messageItemSubject) {
                text = message.subject.ifBlank { context.getString(R.string.message_no_subject) }
                setTextColor(currentTextColor)
                typeface = currentFont
            }
            with(messageItemDate) {
                text = message.date.toFormattedString()
                setTextColor(currentTextColor)
                typeface = currentFont
            }
            with(messageItemAttachmentIcon) {
                ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(currentTextColor))
                isVisible = message.hasAttachments
            }
            messageItemUnreadIndicator.isVisible = message.unread

            root.setOnClickListener {
                holder.bindingAdapterPosition.let {
                    if (it != RecyclerView.NO_POSITION) {
                        onItemClickListener(item, it)
                    }
                }
            }

            root.setOnLongClickListener {
                onLongItemClickListener(item)
                true
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
