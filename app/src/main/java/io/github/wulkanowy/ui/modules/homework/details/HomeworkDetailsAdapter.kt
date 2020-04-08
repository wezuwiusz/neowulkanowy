package io.github.wulkanowy.ui.modules.homework.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.utils.toFormattedString
import kotlinx.android.synthetic.main.item_homework_dialog_attachment.view.*
import kotlinx.android.synthetic.main.item_homework_dialog_details.view.*
import javax.inject.Inject

class HomeworkDetailsAdapter @Inject constructor() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private enum class ViewType(val id: Int) {
        DETAILS(1),
        ATTACHMENTS_HEADER(2),
        ATTACHMENT(3)
    }

    private var attachments = emptyList<Pair<String, String>>()

    var homework: Homework? = null
        set(value) {
            field = value
            attachments = value?.attachments.orEmpty()
        }

    var onAttachmentClickListener: (url: String) -> Unit = {}

    override fun getItemCount() = 1 + if (attachments.isNotEmpty()) attachments.size + 1 else 0

    override fun getItemViewType(position: Int) = when (position) {
        0 -> ViewType.DETAILS.id
        1 -> ViewType.ATTACHMENTS_HEADER.id
        else -> ViewType.ATTACHMENT.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            ViewType.ATTACHMENTS_HEADER.id -> AttachmentsHeaderViewHolder(inflater.inflate(R.layout.item_homework_dialog_attachments_header, parent, false))
            ViewType.ATTACHMENT.id -> AttachmentViewHolder(inflater.inflate(R.layout.item_homework_dialog_attachment, parent, false))
            else -> DetailsViewHolder(inflater.inflate(R.layout.item_homework_dialog_details, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DetailsViewHolder -> bindDetailsViewHolder(holder)
            is AttachmentViewHolder -> bindAttachmentViewHolder(holder, position - 2)
        }
    }

    private fun bindDetailsViewHolder(holder: DetailsViewHolder) {
        with(holder.view) {
            homeworkDialogDate.text = homework?.date?.toFormattedString()
            homeworkDialogEntryDate.text = homework?.entryDate?.toFormattedString()
            homeworkDialogSubject.text = homework?.subject
            homeworkDialogTeacher.text = homework?.teacher
            homeworkDialogContent.text = homework?.content
        }
    }

    private fun bindAttachmentViewHolder(holder: AttachmentViewHolder, position: Int) {
        val item = attachments[position]

        with(holder.view) {
            homeworkDialogAttachment.text = item.second
            setOnClickListener {
                onAttachmentClickListener(item.first)
            }
        }
    }

    class DetailsViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    class AttachmentsHeaderViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    class AttachmentViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}
