package io.github.wulkanowy.ui.main.more

import android.graphics.drawable.Drawable
import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.github.wulkanowy.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_more.*

class MoreItem(val title: String, private val drawable: Drawable?)
    : AbstractFlexibleItem<MoreItem.ViewHolder>() {

    override fun getLayoutRes() = R.layout.item_more

    override fun createViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<*>>?): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>?, holder: ViewHolder?,
                                position: Int, payloads: MutableList<Any>?) {
        holder?.apply {
            moreItemTitle.text = title
            moreItemImage.setImageDrawable(drawable)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MoreItem

        if (title != other.title) return false

        return true
    }

    override fun hashCode(): Int {
        return title.hashCode()
    }

    class ViewHolder(view: View?, adapter: FlexibleAdapter<*>?)
        : FlexibleViewHolder(view, adapter), LayoutContainer {

        override val containerView: View?
            get() = contentView
    }
}
