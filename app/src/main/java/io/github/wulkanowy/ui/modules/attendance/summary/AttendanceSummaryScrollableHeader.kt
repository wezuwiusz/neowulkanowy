package io.github.wulkanowy.ui.modules.attendance.summary

import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.github.wulkanowy.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.scrollable_header_attendance_summary.*

class AttendanceSummaryScrollableHeader(private val percentage: String) :
    AbstractFlexibleItem<AttendanceSummaryScrollableHeader.ViewHolder>() {

    override fun getLayoutRes() = R.layout.scrollable_header_attendance_summary

    override fun createViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<*>>?): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>?, holder: ViewHolder?, position: Int, payloads: MutableList<Any>?) {
        holder?.apply { attendanceSummaryScrollableHeaderPercentage.text = percentage }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AttendanceSummaryScrollableHeader

        if (percentage != other.percentage) return false

        return true
    }

    override fun hashCode(): Int {
        return percentage.hashCode()
    }

    class ViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<*>>?) : FlexibleViewHolder(view, adapter),
        LayoutContainer {

        override val containerView: View?
            get() = contentView
    }
}
