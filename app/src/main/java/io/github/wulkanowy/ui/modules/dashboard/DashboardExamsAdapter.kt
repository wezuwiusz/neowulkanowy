package io.github.wulkanowy.ui.modules.dashboard

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.databinding.SubitemDashboardExamsBinding
import io.github.wulkanowy.utils.getThemeAttrColor
import io.github.wulkanowy.utils.toFormattedString
import java.time.LocalDate

class DashboardExamsAdapter :
    RecyclerView.Adapter<DashboardExamsAdapter.ViewHolder>() {

    var items = emptyList<Exam>()

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        SubitemDashboardExamsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.binding.root.context
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
            dashboardHomeworkSubitemTime.text = item.date.toFormattedString("dd.MM")
            dashboardHomeworkSubitemTime.setTextColor(secondaryWarningTextColor)

            dashboardHomeworkSubitemTitle.text = "${item.type} - ${item.subject}"
            dashboardHomeworkSubitemTitle.setTextColor(primaryWarningTextColor)
        }
    }

    class ViewHolder(val binding: SubitemDashboardExamsBinding) :
        RecyclerView.ViewHolder(binding.root)
}