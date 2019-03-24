package io.github.wulkanowy.ui.modules.attendance.summary

import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.github.wulkanowy.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_attendance_summary.*

class AttendanceSummaryItem(
    private val month: String,
    private val percentage: String,
    private val present: String,
    private val absence: String,
    private val excusedAbsence: String,
    private val schoolAbsence: String,
    private val exemption: String,
    private val lateness: String,
    private val excusedLateness: String
) : AbstractFlexibleItem<AttendanceSummaryItem.ViewHolder>() {

    override fun getLayoutRes() = R.layout.item_attendance_summary

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: ViewHolder, position: Int, payloads: MutableList<Any>?) {
        holder.apply {
            attendanceSummaryMonth.text = month
            attendanceSummaryPercentage.text = percentage
            attendanceSummaryPresent.text = present
            attendanceSummaryAbsenceUnexcused.text = absence
            attendanceSummaryAbsenceExcused.text = excusedAbsence
            attendanceSummaryAbsenceSchool.text = schoolAbsence
            attendanceSummaryExemption.text = exemption
            attendanceSummaryLatenessUnexcused.text = lateness
            attendanceSummaryLatenessExcused.text = excusedLateness
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AttendanceSummaryItem

        if (month != other.month) return false
        if (percentage != other.percentage) return false
        if (present != other.present) return false
        if (absence != other.absence) return false
        if (excusedAbsence != other.excusedAbsence) return false
        if (schoolAbsence != other.schoolAbsence) return false
        if (exemption != other.exemption) return false
        if (lateness != other.lateness) return false
        if (excusedLateness != other.excusedLateness) return false

        return true
    }

    override fun hashCode(): Int {
        var result = month.hashCode()
        result = 31 * result + percentage.hashCode()
        result = 31 * result + present.hashCode()
        result = 31 * result + absence.hashCode()
        result = 31 * result + excusedAbsence.hashCode()
        result = 31 * result + schoolAbsence.hashCode()
        result = 31 * result + exemption.hashCode()
        result = 31 * result + lateness.hashCode()
        result = 31 * result + excusedLateness.hashCode()
        return result
    }

    class ViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter), LayoutContainer {
        override val containerView: View
            get() = contentView
    }
}
