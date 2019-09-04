package io.github.wulkanowy.ui.modules.about

import android.view.View
import androidx.core.content.res.ResourcesCompat
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.github.wulkanowy.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.scrollable_header_about.*

class AboutScrollableHeader : AbstractFlexibleItem<AboutScrollableHeader.ViewHolder>() {

    override fun getLayoutRes() = R.layout.scrollable_header_about

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>) = ViewHolder(view, adapter)

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        with(holder) {
            val context = contentView.context
            val drawable = ResourcesCompat.getDrawableForDensity(context.resources, context.applicationInfo.icon, 640, null)

            aboutScrollableHeaderIcon.setImageDrawable(drawable)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode() = javaClass.hashCode()

    class ViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>) : FlexibleViewHolder(view, adapter),
        LayoutContainer {

        override val containerView: View? get() = contentView
    }
}
