package io.github.wulkanowy.ui.modules.about

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.databinding.ItemAboutBinding
import io.github.wulkanowy.databinding.ScrollableHeaderAboutBinding
import javax.inject.Inject

class AboutAdapter @Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private enum class ViewType(val id: Int) {
        ITEM_HEADER(1),
        ITEM_ELEMENT(2)
    }

    var items = emptyList<Triple<String, String, Drawable?>>()

    var onClickListener: (name: String) -> Unit = {}

    override fun getItemCount() = items.size + 1

    override fun getItemViewType(position: Int) = when (position) {
        0 -> ViewType.ITEM_HEADER.id
        else -> ViewType.ITEM_ELEMENT.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            ViewType.ITEM_HEADER.id -> HeaderViewHolder(ScrollableHeaderAboutBinding.inflate(inflater, parent, false))
            ViewType.ITEM_ELEMENT.id -> ItemViewHolder(ItemAboutBinding.inflate(inflater, parent, false))
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> bindHeaderViewHolder(holder.binding)
            is ItemViewHolder -> bindItemViewHolder(holder.binding, position - 1)
        }
    }

    private fun bindHeaderViewHolder(binding: ScrollableHeaderAboutBinding) {
        with(binding.aboutScrollableHeaderIcon) {
            setImageDrawable(ResourcesCompat.getDrawableForDensity(
                context.resources, context.applicationInfo.icon, 640, null)
            )
        }
    }

    private fun bindItemViewHolder(binding: ItemAboutBinding, position: Int) {
        val (title, summary, image) = items[position]

        with(binding) {
            aboutItemImage.setImageDrawable(image)
            aboutItemTitle.text = title
            aboutItemSummary.text = summary

            root.setOnClickListener { onClickListener(title) }
        }
    }

    private class HeaderViewHolder(val binding: ScrollableHeaderAboutBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class ItemViewHolder(val binding: ItemAboutBinding) :
        RecyclerView.ViewHolder(binding.root)
}
