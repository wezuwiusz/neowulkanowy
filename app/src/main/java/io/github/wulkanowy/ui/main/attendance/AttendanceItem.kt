package io.github.wulkanowy.ui.main.attendance

import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Attendance
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_attendance.*

class AttendanceItem : AbstractFlexibleItem<AttendanceItem.ViewHolder>() {

    lateinit var attendance: Attendance

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun getLayoutRes(): Int = R.layout.item_attendance

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AttendanceItem

        if (attendance != other.attendance) return false
        return true
    }

    override fun hashCode(): Int {
        return attendance.hashCode()
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: ViewHolder,
                                position: Int, payloads: MutableList<Any>?) {
        holder.bind(attendance)
    }

    class ViewHolder(val view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter),
            LayoutContainer {

        override val containerView: View
            get() = contentView

        fun bind(lesson: Attendance) {
            attendanceItemNumber.text = lesson.number.toString()
            attendanceItemSubject.text = lesson.subject
            attendanceItemDescription.text = lesson.name
            attendanceItemAlert.visibility = if (lesson.absence && !lesson.excused) View.VISIBLE else View.INVISIBLE
        }
    }
}
