package io.github.wulkanowy.ui.modules.message.tab

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.repositories.message.MessageFolder
import io.github.wulkanowy.databinding.ItemMessageBinding
import io.github.wulkanowy.utils.toFormattedString
import javax.inject.Inject

class MessageTabAdapter @Inject constructor() :
    RecyclerView.Adapter<MessageTabAdapter.ItemViewHolder>() {

    var items = mutableListOf<Message>()

    var onClickListener: (Message, position: Int) -> Unit = { _, _ -> }

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

            root.setOnClickListener { onClickListener(item, position) }
        }
    }

    class ItemViewHolder(val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root)
}
