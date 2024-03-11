package io.github.wulkanowy.ui.modules.attendance

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.data.enums.SentExcuseStatus
import io.github.wulkanowy.databinding.ItemAttendanceBinding
import io.github.wulkanowy.utils.descriptionRes
import io.github.wulkanowy.utils.getThemeAttrColor
import io.github.wulkanowy.utils.isExcusableOrNotExcused
import javax.inject.Inject

class AttendanceAdapter @Inject constructor() :
    RecyclerView.Adapter<AttendanceAdapter.ItemViewHolder>() {

    var items = emptyList<Attendance>()

    var excuseActionMode: Boolean = false

    var onClickListener: (Attendance) -> Unit = {}

    var onExcuseCheckboxSelect: (attendanceItem: Attendance, checked: Boolean) -> Unit = { _, _ -> }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemAttendanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val context = holder.binding.root.context
        val item = items[position]

        with(holder.binding) {
            attendanceItemNumber.text = item.number.toString()
            attendanceItemSubject.text = item.subject
                .ifBlank { context.getString(R.string.all_no_data) }
            attendanceItemDescription.setText(item.descriptionRes)

            attendanceItemDescription.setTextColor(
                context.getThemeAttrColor(
                    when {
                        item.absence && !item.excused -> R.attr.colorAttendanceAbsence
                        item.lateness && !item.excused -> R.attr.colorAttendanceLateness
                        else -> android.R.attr.textColorSecondary
                    }
                )
            )

            if (item.exemption || item.excused) {
                attendanceItemDescription.setTypeface(null, Typeface.BOLD)
            } else {
                attendanceItemDescription.setTypeface(null, Typeface.NORMAL)
            }

            attendanceItemAlert.isVisible =
                item.let { (it.absence && !it.excused) || (it.lateness && !it.excused) }

            attendanceItemAlert.imageTintList = ColorStateList.valueOf(
                context.getThemeAttrColor(
                    when {
                        item.absence && !item.excused -> R.attr.colorAttendanceAbsence
                        item.lateness && !item.excused -> R.attr.colorAttendanceLateness
                        else -> android.R.attr.colorPrimary
                    }
                )
            )
            attendanceItemNumber.visibility = View.GONE
            attendanceItemExcuseInfo.visibility = View.GONE
            attendanceItemExcuseCheckbox.visibility = View.GONE
            attendanceItemExcuseCheckbox.isChecked = false
            attendanceItemExcuseCheckbox.setOnCheckedChangeListener { _, checked ->
                onExcuseCheckboxSelect(item, checked)
            }

            when (item.excuseStatus?.let { SentExcuseStatus.valueOf(it) }) {
                SentExcuseStatus.WAITING -> {
                    attendanceItemExcuseInfo.setImageResource(R.drawable.ic_excuse_waiting)
                    attendanceItemExcuseInfo.visibility = View.VISIBLE
                    attendanceItemAlert.visibility = View.INVISIBLE
                }

                SentExcuseStatus.DENIED -> {
                    attendanceItemExcuseInfo.setImageResource(R.drawable.ic_excuse_denied)
                    attendanceItemExcuseInfo.visibility = View.VISIBLE
                }

                else -> {
                    if (item.isExcusableOrNotExcused && excuseActionMode) {
                        attendanceItemNumber.visibility = View.GONE
                        attendanceItemExcuseCheckbox.visibility = View.VISIBLE
                    } else {
                        attendanceItemNumber.visibility = View.VISIBLE
                        attendanceItemExcuseCheckbox.visibility = View.GONE
                    }
                }
            }
            root.setOnClickListener {
                onClickListener(item)

                with(attendanceItemExcuseCheckbox) {
                    if (excuseActionMode && isVisible) {
                        isChecked = !isChecked
                    }
                }
            }
        }
    }

    class ItemViewHolder(val binding: ItemAttendanceBinding) : RecyclerView.ViewHolder(binding.root)
}
