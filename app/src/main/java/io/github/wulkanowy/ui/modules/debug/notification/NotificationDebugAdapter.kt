package io.github.wulkanowy.ui.modules.debug.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.databinding.ItemDebugNotificationsBinding

class NotificationDebugAdapter : RecyclerView.Adapter<NotificationDebugAdapter.ItemViewHolder>() {

    var items = emptyList<NotificationDebugItem>()

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemDebugNotificationsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]

        with(holder.binding) {
            title.setText(item.title)
            button1.setOnClickListener { item.onClickCallback(1) }
            button2.setOnClickListener { item.onClickCallback(3) }
            button3.setOnClickListener { item.onClickCallback(10) }
        }
    }

    class ItemViewHolder(val binding: ItemDebugNotificationsBinding) :
        RecyclerView.ViewHolder(binding.root)
}
