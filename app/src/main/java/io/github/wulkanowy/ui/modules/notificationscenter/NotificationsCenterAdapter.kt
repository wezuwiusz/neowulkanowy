package io.github.wulkanowy.ui.modules.notificationscenter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Notification
import io.github.wulkanowy.databinding.ItemNotificationsCenterBinding
import io.github.wulkanowy.services.sync.notifications.NotificationType
import io.github.wulkanowy.utils.toFormattedString
import javax.inject.Inject

class NotificationsCenterAdapter @Inject constructor() :
    ListAdapter<Notification, NotificationsCenterAdapter.ViewHolder>(DiffUtilCallback()) {

    var onItemClickListener: (NotificationType) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemNotificationsCenterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        with(holder.binding) {
            notificationsCenterItemTitle.text = item.title
            notificationsCenterItemContent.text = item.content
            notificationsCenterItemDate.text = item.date.toFormattedString("HH:mm, d MMM")
            notificationsCenterItemIcon.setImageResource(item.type.toDrawableResId())

            root.setOnClickListener { onItemClickListener(item.type) }
        }
    }

    private fun NotificationType.toDrawableResId() = when (this) {
        NotificationType.NEW_CONFERENCE -> R.drawable.ic_more_conferences
        NotificationType.NEW_EXAM -> R.drawable.ic_main_exam
        NotificationType.NEW_GRADE_DETAILS -> R.drawable.ic_stat_grade
        NotificationType.NEW_GRADE_PREDICTED -> R.drawable.ic_stat_grade
        NotificationType.NEW_GRADE_FINAL -> R.drawable.ic_stat_grade
        NotificationType.NEW_HOMEWORK -> R.drawable.ic_more_homework
        NotificationType.NEW_LUCKY_NUMBER -> R.drawable.ic_stat_luckynumber
        NotificationType.NEW_MESSAGE -> R.drawable.ic_stat_message
        NotificationType.NEW_NOTE -> R.drawable.ic_stat_note
        NotificationType.NEW_ANNOUNCEMENT -> R.drawable.ic_all_about
        NotificationType.PUSH -> R.drawable.ic_stat_all
    }

    class ViewHolder(val binding: ItemNotificationsCenterBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class DiffUtilCallback : DiffUtil.ItemCallback<Notification>() {

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification) =
            oldItem == newItem

        override fun areItemsTheSame(oldItem: Notification, newItem: Notification) =
            oldItem.id == newItem.id
    }
}