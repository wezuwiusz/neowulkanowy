package io.github.wulkanowy.ui.modules.about.license

import android.view.View
import com.mikepenz.aboutlibraries.entity.Library
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.github.wulkanowy.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_license.*

class LicenseItem(val library: Library) : AbstractFlexibleItem<LicenseItem.ViewHolder>() {

    override fun getLayoutRes() = R.layout.item_license

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>) = ViewHolder(view, adapter)

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        with(holder) {
            licenseItemName.text = library.libraryName
            licenseItemSummary.text = library.license?.licenseName?.takeIf { it.isNotBlank() } ?: library.libraryVersion
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LicenseItem

        if (library != other.library) return false

        return true
    }

    override fun hashCode() = library.hashCode()

    class ViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>) : FlexibleViewHolder(view, adapter),
        LayoutContainer {

        override val containerView: View? get() = contentView
    }
}
