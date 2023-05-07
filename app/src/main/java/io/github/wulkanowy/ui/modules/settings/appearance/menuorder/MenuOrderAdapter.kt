package io.github.wulkanowy.ui.modules.settings.appearance.menuorder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.databinding.ItemMenuOrderBinding
import javax.inject.Inject

class MenuOrderAdapter @Inject constructor() :
    RecyclerView.Adapter<MenuOrderAdapter.ViewHolder>() {

    val items = mutableListOf<MenuOrderItem>()

    fun submitList(newItems: List<MenuOrderItem>) {
        val diffResult = DiffUtil.calculateDiff(DiffCallback(newItems, items.toMutableList()))

        with(items) {
            clear()
            addAll(newItems)
        }

        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemMenuOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position].appMenuItem

        with(holder.binding) {
            menuOrderItemTitle.setText(item.title)
            menuOrderItemIcon.setImageResource(item.icon)
        }
    }

    class ViewHolder(val binding: ItemMenuOrderBinding) : RecyclerView.ViewHolder(binding.root)

    private class DiffCallback(
        private val oldList: List<MenuOrderItem>,
        private val newList: List<MenuOrderItem>
    ) : DiffUtil.Callback() {

        override fun getNewListSize() = newList.size

        override fun getOldListSize() = oldList.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition] == newList[newItemPosition]

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition].appMenuItem.destinationType == newList[newItemPosition].appMenuItem.destinationType
    }
}
