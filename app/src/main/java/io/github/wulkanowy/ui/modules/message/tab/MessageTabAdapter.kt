package io.github.wulkanowy.ui.modules.message.tab

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.enums.MessageFolder
import io.github.wulkanowy.databinding.ItemMessageBinding
import io.github.wulkanowy.utils.toFormattedString
import javax.inject.Inject

class MessageTabAdapter @Inject constructor() :
    RecyclerView.Adapter<MessageTabAdapter.ItemViewHolder>() {

    var onClickListener: (Message, position: Int) -> Unit = { _, _ -> }

    var onChangesDetectedListener = {}

    private var items = mutableListOf<Message>()

    fun setDataItems(data: List<Message>) {
        if (items.size != data.size) onChangesDetectedListener()
        val diffResult = DiffUtil.calculateDiff(MessageTabDiffUtil(items, data))
        items = data.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]

        with(holder.binding) {
            val style = if (item.unread) Typeface.BOLD else Typeface.NORMAL

            messageItemAuthor.run {
                text = if (item.folderId == MessageFolder.SENT.id) item.recipient else item.sender
                setTypeface(null, style)
            }
            messageItemSubject.run {
                text = if (item.subject.isNotBlank()) item.subject else context.getString(R.string.message_no_subject)
                setTypeface(null, style)
            }
            messageItemDate.run {
                text = item.date.toFormattedString()
                setTypeface(null, style)
            }
            messageItemAttachmentIcon.visibility = if (item.hasAttachments) View.VISIBLE else View.GONE

            root.setOnClickListener {
                holder.bindingAdapterPosition.let { if (it != NO_POSITION) onClickListener(item, it) }
            }
        }
    }

    class ItemViewHolder(val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root)

    private class MessageTabDiffUtil(private val old: List<Message>, private val new: List<Message>) :
        DiffUtil.Callback() {
        override fun getOldListSize(): Int = old.size

        override fun getNewListSize(): Int = new.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return old[oldItemPosition].id == new[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return old[oldItemPosition] == new[newItemPosition]
        }
    }
}
