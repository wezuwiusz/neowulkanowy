package io.github.wulkanowy.ui.modules.about

import android.graphics.drawable.Drawable
import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.github.wulkanowy.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_about.*

class AboutItem(
    val title: String,
    private val summary: String,
    private val image: Drawable?
) : AbstractFlexibleItem<AboutItem.ViewHolder>() {

    override fun getLayoutRes() = R.layout.item_about

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>) = ViewHolder(view, adapter)

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        with(holder) {
            aboutItemImage.setImageDrawable(image)
            aboutItemTitle.text = title
            aboutItemSummary.text = summary
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AboutItem

        if (title != other.title) return false
        if (summary != other.summary) return false
        if (image != other.image) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + summary.hashCode()
        result = 31 * result + (image?.hashCode() ?: 0)
        return result
    }

    class ViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>) : FlexibleViewHolder(view, adapter),
        LayoutContainer {

        override val containerView: View? get() = contentView
    }
}
