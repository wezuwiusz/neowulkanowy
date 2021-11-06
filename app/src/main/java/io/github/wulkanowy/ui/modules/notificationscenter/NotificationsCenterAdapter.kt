package io.github.wulkanowy.ui.modules.notificationscenter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
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
            notificationsCenterItemIcon.setImageResource(item.type.icon)

            root.setOnClickListener { onItemClickListener(item.type) }
        }
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
