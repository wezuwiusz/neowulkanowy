package io.github.wulkanowy.ui.modules.message.tab

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import androidx.recyclerview.widget.SortedListAdapterCallback
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.repositories.message.MessageFolder
import io.github.wulkanowy.databinding.ItemMessageBinding
import io.github.wulkanowy.utils.toFormattedString
import javax.inject.Inject

class MessageTabAdapter @Inject constructor() :
    RecyclerView.Adapter<MessageTabAdapter.ItemViewHolder>() {

    var onClickListener: (Message, position: Int) -> Unit = { _, _ -> }

    private val items = SortedList(Message::class.java, object :
        SortedListAdapterCallback<Message>(this) {

        override fun compare(item1: Message, item2: Message): Int {
            return item2.date.compareTo(item1.date)
        }

        override fun areContentsTheSame(oldItem: Message?, newItem: Message?): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(item1: Message, item2: Message): Boolean {
            return item1 == item2
        }
    })

    fun replaceAll(models: List<Message>) {
        items.beginBatchedUpdates()
        for (i in items.size() - 1 downTo 0) {
            val model = items.get(i)
            if (model !in models) {
                items.remove(model)
            }
        }
        items.addAll(models)
        items.endBatchedUpdates()
    }

    fun updateItem(position: Int, item: Message) {
        items.updateItemAt(position, item)
    }

    override fun getItemCount() = items.size()

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

            root.setOnClickListener { onClickListener(item, holder.adapterPosition) }
        }
    }

    class ItemViewHolder(val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root)
}
