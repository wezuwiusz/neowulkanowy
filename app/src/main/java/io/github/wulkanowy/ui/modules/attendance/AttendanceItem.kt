package io.github.wulkanowy.ui.modules.attendance

import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.core.view.isVisible
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.data.repositories.attendance.SentExcuseStatus
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_attendance.*

class AttendanceItem(val attendance: Attendance) :
    AbstractFlexibleItem<AttendanceItem.ViewHolder>() {

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
            attendanceItemNumber.visibility = GONE
            attendanceItemExcuseInfo.visibility = GONE
            attendanceItemExcuseCheckbox.visibility = GONE
            attendanceItemExcuseCheckbox.isChecked = false
            attendanceItemExcuseCheckbox.setOnCheckedChangeListener { _, checked ->
                (adapter as AttendanceAdapter).onExcuseCheckboxSelect(attendance, checked)
            }

            when (if (attendance.excuseStatus != null) SentExcuseStatus.valueOf(attendance.excuseStatus) else null) {
                SentExcuseStatus.WAITING -> {
                    attendanceItemExcuseInfo.setImageResource(R.drawable.ic_excuse_waiting)
                    attendanceItemExcuseInfo.visibility = VISIBLE
                    attendanceItemAlert.visibility = INVISIBLE
                }
                SentExcuseStatus.DENIED -> {
                    attendanceItemExcuseInfo.setImageResource(R.drawable.ic_excuse_denied)
                    attendanceItemExcuseInfo.visibility = VISIBLE
                }
                else -> {
                    if (attendance.excusable && (adapter as AttendanceAdapter).excuseActionMode) {
                        attendanceItemNumber.visibility = GONE
                        attendanceItemExcuseCheckbox.visibility = VISIBLE
                    } else {
                        attendanceItemNumber.visibility = VISIBLE
                        attendanceItemExcuseCheckbox.visibility = GONE
                    }
                }
            }
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

    class ViewHolder(view: View, val adapter: FlexibleAdapter<*>) :
        FlexibleViewHolder(view, adapter),
        LayoutContainer {

        override val containerView: View
            get() = contentView

        override fun onClick(view: View?) {
            super.onClick(view)
            attendanceItemExcuseCheckbox.apply {
                if ((adapter as AttendanceAdapter).excuseActionMode && isVisible) {
                    isChecked = !isChecked
                }
            }
        }
    }
}
