package io.github.wulkanowy.ui.modules.more

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.databinding.ItemMoreBinding
import javax.inject.Inject

class MoreAdapter @Inject constructor() : RecyclerView.Adapter<MoreAdapter.ItemViewHolder>() {

    var items = emptyList<Pair<String, Drawable?>>()

    var onClickListener: (name: String) -> Unit = {}

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val (title, drawable) = items[position]

        with(holder.binding) {
            moreItemTitle.text = title
            moreItemImage.setImageDrawable(drawable)

            root.setOnClickListener { onClickListener(title) }
        }
    }

    class ItemViewHolder(val binding: ItemMoreBinding) : RecyclerView.ViewHolder(binding.root)
}
