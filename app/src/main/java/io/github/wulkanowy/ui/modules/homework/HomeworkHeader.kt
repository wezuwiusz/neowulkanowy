package io.github.wulkanowy.ui.modules.homework

import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractHeaderItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.ExpandableViewHolder
import io.github.wulkanowy.R
import io.github.wulkanowy.utils.toFormattedString
import io.github.wulkanowy.utils.weekDayName
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.header_homework.*
import org.threeten.bp.LocalDate

class HomeworkHeader(private val date: LocalDate) : AbstractHeaderItem<HomeworkHeader.ViewHolder>() {

    override fun getLayoutRes() = R.layout.header_homework

    override fun createViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<*>>?): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<*>>?, holder: HomeworkHeader.ViewHolder,
        position: Int, payloads: MutableList<Any>?
    ) {
        holder.run {
            homeworkHeaderDay.text = date.weekDayName.capitalize()
            homeworkHeaderDate.text = date.toFormattedString()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HomeworkHeader

        if (date != other.date) return false

        return true
    }

    override fun hashCode(): Int {
        return date.hashCode()
    }

    class ViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<*>>?) : ExpandableViewHolder(view, adapter),
        LayoutContainer {

        override val containerView: View
            get() = contentView
    }
}
