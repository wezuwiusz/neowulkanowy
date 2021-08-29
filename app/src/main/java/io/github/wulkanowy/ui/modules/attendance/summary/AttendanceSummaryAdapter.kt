package io.github.wulkanowy.ui.modules.attendance.summary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.AttendanceSummary
import io.github.wulkanowy.databinding.ItemAttendanceSummaryBinding
import io.github.wulkanowy.databinding.ScrollableHeaderAttendanceSummaryBinding
import io.github.wulkanowy.utils.calculatePercentage
import io.github.wulkanowy.utils.getFormattedName
import java.time.Month
import java.util.Locale
import javax.inject.Inject

class AttendanceSummaryAdapter @Inject constructor() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private enum class ViewType(val id: Int) {
        HEADER(1),
        ITEM(2)
    }

    var items = emptyList<AttendanceSummary>()

    override fun getItemCount() = if (items.isNotEmpty()) items.size + 2 else 0

    override fun getItemViewType(position: Int) = when (position) {
        0 -> ViewType.HEADER.id
        else -> ViewType.ITEM.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            ViewType.HEADER.id -> HeaderViewHolder(ScrollableHeaderAttendanceSummaryBinding.inflate(inflater, parent, false))
            ViewType.ITEM.id -> ItemViewHolder(ItemAttendanceSummaryBinding.inflate(inflater, parent, false))
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> bindHeaderViewHolder(holder.binding)
            is ItemViewHolder -> bindItemViewHolder(holder.binding, position - 2)
        }
    }

    private fun bindHeaderViewHolder(binding: ScrollableHeaderAttendanceSummaryBinding) {
        binding.attendanceSummaryScrollableHeaderPercentage.text = formatPercentage(items.calculatePercentage())
    }

    private fun bindItemViewHolder(binding: ItemAttendanceSummaryBinding, position: Int) {
        val item = if (position == -1) getTotalItem() else items[position]

        with(binding) {
            attendanceSummaryMonth.text = when (position) {
                -1 -> root.context.getString(R.string.attendance_summary_total)
                else -> item.month.getFormattedName()
            }
            attendanceSummaryPercentage.text = when (position) {
                -1 -> formatPercentage(items.calculatePercentage())
                else -> formatPercentage(item.calculatePercentage())
            }

            attendanceSummaryPresent.text = item.presence.toString()
            attendanceSummaryAbsenceUnexcused.text = item.absence.toString()
            attendanceSummaryAbsenceExcused.text = item.absenceExcused.toString()
            attendanceSummaryAbsenceSchool.text = item.absenceForSchoolReasons.toString()
            attendanceSummaryExemption.text = item.exemption.toString()
            attendanceSummaryLatenessUnexcused.text = item.lateness.toString()
            attendanceSummaryLatenessExcused.text = item.latenessExcused.toString()
        }
    }

    private fun getTotalItem() = AttendanceSummary(
        month = Month.APRIL,
        presence = items.sumOf { it.presence },
        absence = items.sumOf { it.absence },
        absenceExcused = items.sumOf { it.absenceExcused },
        absenceForSchoolReasons = items.sumOf { it.absenceForSchoolReasons },
        exemption = items.sumOf { it.exemption },
        lateness = items.sumOf { it.lateness },
        latenessExcused = items.sumOf { it.latenessExcused },
        diaryId = -1,
        studentId = -1,
        subjectId = -1
    )

    private fun formatPercentage(percentage: Double): String {
        return if (percentage == 0.0) "0%"
        else "${String.format(Locale.FRANCE, "%.2f", percentage)}%"
    }

    class HeaderViewHolder(val binding: ScrollableHeaderAttendanceSummaryBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ItemViewHolder(val binding: ItemAttendanceSummaryBinding) :
        RecyclerView.ViewHolder(binding.root)
}
