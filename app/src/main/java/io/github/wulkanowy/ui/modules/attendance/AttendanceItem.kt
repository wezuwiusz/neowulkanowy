package io.github.wulkanowy.ui.modules.attendance

import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Attendance
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_attendance.*

class AttendanceItem(val attendance: Attendance) : AbstractFlexibleItem<AttendanceItem.ViewHolder>() {

    override fun getLayoutRes() = R.layout.item_attendance

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: ViewHolder, position: Int, payloads: MutableList<Any>?) {
        holder.apply {
            attendanceItemNumber.text = attendance.number.toString()
            attendanceItemSubject.text = attendance.subject
            attendanceItemDescription.text = attendance.name
            attendanceItemAlert.visibility = attendance.run { if (absence && !excused) VISIBLE else INVISIBLE }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AttendanceItem

        if (attendance != other.attendance) return false

        return true
    }

    override fun hashCode(): Int {
        var result = attendance.hashCode()
        result = 31 * result + attendance.id.toInt()
        return result
    }

    class ViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter), LayoutContainer {
        override val containerView: View
            get() = contentView
    }
}
