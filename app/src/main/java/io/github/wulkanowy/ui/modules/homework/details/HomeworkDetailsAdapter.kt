package io.github.wulkanowy.ui.modules.homework.details

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.databinding.ItemHomeworkDialogAttachmentBinding
import io.github.wulkanowy.databinding.ItemHomeworkDialogAttachmentsHeaderBinding
import io.github.wulkanowy.databinding.ItemHomeworkDialogDetailsBinding
import io.github.wulkanowy.utils.ifNullOrBlank
import io.github.wulkanowy.utils.toFormattedString
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

    var isHomeworkFullscreen = false

    var onAttachmentClickListener: (url: String) -> Unit = {}

    var onFullScreenClickListener = {}

    var onFullScreenExitClickListener = {}

    var onDeleteClickListener: (homework: Homework) -> Unit = {}

    override fun getItemCount() = 1 + if (attachments.isNotEmpty()) attachments.size + 1 else 0

    override fun getItemViewType(position: Int) = when (position) {
        0 -> ViewType.DETAILS.id
        1 -> ViewType.ATTACHMENTS_HEADER.id
        else -> ViewType.ATTACHMENT.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            ViewType.ATTACHMENTS_HEADER.id -> AttachmentsHeaderViewHolder(
                ItemHomeworkDialogAttachmentsHeaderBinding.inflate(inflater, parent, false)
            )
            ViewType.ATTACHMENT.id -> AttachmentViewHolder(
                ItemHomeworkDialogAttachmentBinding.inflate(inflater, parent, false)
            )
            else -> DetailsViewHolder(
                ItemHomeworkDialogDetailsBinding.inflate(inflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DetailsViewHolder -> bindDetailsViewHolder(holder)
            is AttachmentViewHolder -> bindAttachmentViewHolder(holder, position - 2)
        }
    }

    private fun bindDetailsViewHolder(holder: DetailsViewHolder) {
        val noDataString = holder.binding.root.context.getString(R.string.all_no_data)

        with(holder.binding) {
            homeworkDialogDate.text = homework?.date?.toFormattedString()
            homeworkDialogEntryDate.text = homework?.entryDate?.toFormattedString()
            homeworkDialogSubject.text = homework?.subject.ifNullOrBlank { noDataString }
            homeworkDialogTeacher.text = homework?.teacher.ifNullOrBlank { noDataString }
            homeworkDialogContent.text = homework?.content.ifNullOrBlank { noDataString }
            homeworkDialogDelete.visibility = if (homework?.isAddedByUser == true) VISIBLE else GONE
            homeworkDialogFullScreen.visibility = if (isHomeworkFullscreen) GONE else VISIBLE
            homeworkDialogFullScreenExit.visibility = if (isHomeworkFullscreen) VISIBLE else GONE
            homeworkDialogFullScreen.setOnClickListener {
                homeworkDialogFullScreen.visibility = GONE
                homeworkDialogFullScreenExit.visibility = VISIBLE
                onFullScreenClickListener()
            }
            homeworkDialogFullScreenExit.setOnClickListener {
                homeworkDialogFullScreen.visibility = VISIBLE
                homeworkDialogFullScreenExit.visibility = GONE
                onFullScreenExitClickListener()
            }
            homeworkDialogDelete.setOnClickListener {
                onDeleteClickListener(homework!!)
            }
        }
    }

    private fun bindAttachmentViewHolder(holder: AttachmentViewHolder, position: Int) {
        val item = attachments[position]

        with(holder.binding) {
            homeworkDialogAttachment.text = item.second
            root.setOnClickListener {
                onAttachmentClickListener(item.first)
            }
        }
    }

    class DetailsViewHolder(val binding: ItemHomeworkDialogDetailsBinding) :
        RecyclerView.ViewHolder(binding.root)

    class AttachmentsHeaderViewHolder(val binding: ItemHomeworkDialogAttachmentsHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)

    class AttachmentViewHolder(val binding: ItemHomeworkDialogAttachmentBinding) :
        RecyclerView.ViewHolder(binding.root)
}
