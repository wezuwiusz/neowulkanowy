package io.github.wulkanowy.ui.modules.debug

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.databinding.ItemDebugBinding

class DebugAdapter : RecyclerView.Adapter<DebugAdapter.ItemViewHolder>() {

    var items = emptyList<DebugItem>()

    var onItemClickListener: (DebugItem) -> Unit = {}

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemDebugBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            debugItemName.setText(item.title)

            root.setOnClickListener {
                onItemClickListener(item)
            }
        }
    }

    class ItemViewHolder(val binding: ItemDebugBinding) : RecyclerView.ViewHolder(binding.root)
}
