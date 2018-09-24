package io.github.wulkanowy.ui.main.exam

import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractHeaderItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.ExpandableViewHolder
import io.github.wulkanowy.R
import io.github.wulkanowy.utils.extension.getWeekDayName
import io.github.wulkanowy.utils.extension.toFormat
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.header_exam.*
import org.apache.commons.lang3.StringUtils
import java.util.*

class ExamHeader : AbstractHeaderItem<ExamHeader.ViewHolder>() {

    lateinit var date: Date

    override fun createViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<*>>?): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun getLayoutRes() = R.layout.header_exam

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExamHeader

        if (date != other.date) return false

        return true
    }

    override fun hashCode(): Int {
        return date.hashCode()
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>?, holder: ViewHolder,
                                position: Int, payloads: MutableList<Any>?) {
        holder.run {
            examHeaderDay.text = StringUtils.capitalize(date.getWeekDayName())
            examHeaderDate.text = date.toFormat()
        }
    }

    class ViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<*>>?) : ExpandableViewHolder(view, adapter),
            LayoutContainer {

        init {
            contentView.setOnClickListener(this)
        }

        override val containerView: View
            get() = contentView
    }
}
