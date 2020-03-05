package io.github.wulkanowy.ui.modules.about.contributor

import android.view.View
import coil.api.load
import coil.transform.RoundedCornersTransformation
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.github.wulkanowy.R
import io.github.wulkanowy.data.pojos.AppCreator
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_contributor.*

class ContributorItem(val creator: AppCreator) :
    AbstractFlexibleItem<ContributorItem.ViewHolder>() {

    override fun getLayoutRes() = R.layout.item_contributor

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>) = ViewHolder(view, adapter)

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        with(holder) {
            creatorItemName.text = creator.displayName

            creatorItemAvatar.load("https://github.com/${creator.githubUsername}.png") {
                transformations(RoundedCornersTransformation(8f))
                crossfade(true)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ContributorItem

        if (creator != other.creator) return false

        return true
    }

    override fun hashCode() = creator.hashCode()

    class ViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>) : FlexibleViewHolder(view, adapter),
        LayoutContainer {

        override val containerView: View? get() = contentView
    }
}
