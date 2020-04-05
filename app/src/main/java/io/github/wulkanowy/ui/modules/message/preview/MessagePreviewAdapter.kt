package io.github.wulkanowy.ui.modules.message.preview

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.MessageAttachment
import io.github.wulkanowy.data.db.entities.MessageWithAttachment
import io.github.wulkanowy.data.repositories.message.MessageFolder
import io.github.wulkanowy.utils.openInternetBrowser
import io.github.wulkanowy.utils.toFormattedString
import kotlinx.android.synthetic.main.item_message_attachment.view.*
import kotlinx.android.synthetic.main.item_message_preview.view.*
import javax.inject.Inject

class MessagePreviewAdapter @Inject constructor() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class ViewType(val id: Int) {
        MESSAGE(1),
        DIVIDER(2),
        ATTACHMENT(3)
    }

    var messageWithAttachment: MessageWithAttachment? = null
        set(value) {
            field = value
            attachments = value?.attachments.orEmpty()
        }

    private var attachments: List<MessageAttachment> = emptyList()

    override fun getItemCount() = if (messageWithAttachment == null) 0 else attachments.size + 1 + if (attachments.isNotEmpty()) 1 else 0

    override fun getItemViewType(position: Int) = when (position) {
        0 -> ViewType.MESSAGE.id
        1 -> ViewType.DIVIDER.id
        else -> ViewType.ATTACHMENT.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            ViewType.MESSAGE.id -> MessageViewHolder(inflater.inflate(R.layout.item_message_preview, parent, false))
            ViewType.DIVIDER.id -> DividerViewHolder(inflater.inflate(R.layout.item_message_divider, parent, false))
            ViewType.ATTACHMENT.id -> AttachmentViewHolder(inflater.inflate(R.layout.item_message_attachment, parent, false))
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MessageViewHolder -> bindMessage(holder.view, requireNotNull(messageWithAttachment).message)
            is AttachmentViewHolder -> bindAttachment(holder.view, requireNotNull(messageWithAttachment).attachments[position - 2])
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindMessage(view: View, message: Message) {
        with(view) {
            messagePreviewSubject.text = if (message.subject.isNotBlank()) message.subject else context.getString(R.string.message_no_subject)
            messagePreviewDate.text = context.getString(R.string.message_date, message.date.toFormattedString("yyyy-MM-dd HH:mm:ss"))
            messagePreviewContent.text = message.content
            messagePreviewAuthor.text = if (message.folderId == MessageFolder.SENT.id) "${context.getString(R.string.message_to)} ${message.recipient}"
            else "${context.getString(R.string.message_from)} ${message.sender}"
        }
    }

    private fun bindAttachment(view: View, attachment: MessageAttachment) {
        with(view) {
            messagePreviewAttachment.visibility = View.VISIBLE
            messagePreviewAttachment.text = attachment.filename
            setOnClickListener {
                context.openInternetBrowser(attachment.url) { }
            }
        }
    }

    class MessageViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    class DividerViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    class AttachmentViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}
