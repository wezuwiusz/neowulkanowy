package io.github.wulkanowy.ui.modules.dashboard

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.databinding.SubitemDashboardHomeworkBinding
import io.github.wulkanowy.utils.getThemeAttrColor
import io.github.wulkanowy.utils.toFormattedString
import java.time.LocalDate

class DashboardHomeworkAdapter : RecyclerView.Adapter<DashboardHomeworkAdapter.ViewHolder>() {

    var items = emptyList<Homework>()

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        SubitemDashboardHomeworkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.binding.root.context
        val formattedDate = item.date.toFormattedString("dd.MM")
        val primaryWarningTextColor = context.getThemeAttrColor(
            if (item.date == LocalDate.now()) {
                R.attr.colorPrimary
            } else {
                android.R.attr.textColorPrimary
            }
        )
        val secondaryWarningTextColor = context.getThemeAttrColor(
            if (item.date == LocalDate.now()) {
                R.attr.colorPrimary
            } else {
                android.R.attr.textColorSecondary
            }
        )

        with(holder.binding) {
            dashboardHomeworkSubitemTitle.text = "${item.subject} - ${item.content}"
            dashboardHomeworkSubitemTitle.setTextColor(primaryWarningTextColor)

            dashboardHomeworkSubitemTime.text =
                context.getString(R.string.dashboard_homework_time, formattedDate)
            dashboardHomeworkSubitemTime.setTextColor(secondaryWarningTextColor)
        }
    }

    class ViewHolder(val binding: SubitemDashboardHomeworkBinding) :
        RecyclerView.ViewHolder(binding.root)
}