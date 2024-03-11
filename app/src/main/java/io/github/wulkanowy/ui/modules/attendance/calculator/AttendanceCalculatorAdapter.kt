package io.github.wulkanowy.ui.modules.attendance.calculator

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.R
import io.github.wulkanowy.data.pojos.AttendanceData
import io.github.wulkanowy.databinding.ItemAttendanceCalculatorHeaderBinding
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.roundToInt

class AttendanceCalculatorAdapter @Inject constructor() :
    RecyclerView.Adapter<AttendanceCalculatorAdapter.ViewHolder>() {

    var items = emptyList<AttendanceData>()

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemAttendanceCalculatorHeaderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(parent: ViewHolder, position: Int) {
        val context = parent.binding.root.context
        val item = items[position]

        with(parent.binding) {
            attendanceCalculatorPercentage.text = "${item.presencePercentage.roundToInt()}"

            attendanceCalculatorSummaryBalance.text = when {
                item.lessonBalance > 0 -> {
                    context.getString(
                        R.string.attendance_calculator_summary_balance_positive,
                        item.lessonBalance
                    )
                }

                item.lessonBalance < 0 -> {
                    context.getString(
                        R.string.attendance_calculator_summary_balance_negative,
                        abs(item.lessonBalance)
                    )
                }

                else -> context.getString(R.string.attendance_calculator_summary_balance_neutral)
            }
            attendanceCalculatorWarning.isVisible = item.lessonBalance < 0
            attendanceCalculatorTitle.text = item.subjectName
            attendanceCalculatorSummaryValues.text = if (item.total == 0) {
                context.getString(R.string.attendance_calculator_summary_values_empty)
            } else {
                context.getString(
                    R.string.attendance_calculator_summary_values,
                    item.presences,
                    item.total
                )
            }
        }
    }

    class ViewHolder(val binding: ItemAttendanceCalculatorHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)
}
