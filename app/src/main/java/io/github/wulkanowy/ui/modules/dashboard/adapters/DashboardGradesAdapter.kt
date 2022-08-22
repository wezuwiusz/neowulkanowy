package io.github.wulkanowy.ui.modules.dashboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.enums.GradeColorTheme
import io.github.wulkanowy.databinding.SubitemDashboardGradesBinding
import io.github.wulkanowy.databinding.SubitemDashboardSmallGradeBinding
import io.github.wulkanowy.utils.getBackgroundColor

class DashboardGradesAdapter : RecyclerView.Adapter<DashboardGradesAdapter.ViewHolder>() {

    var items = listOf<Pair<String, List<Grade>>>()

    lateinit var gradeColorTheme: GradeColorTheme

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        SubitemDashboardGradesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (subject, grades) = items[position]
        val context = holder.binding.root.context

        with(holder.binding) {
            dashboardGradesSubitemTitle.text = subject

            grades.forEach {
                val subitemBinding = SubitemDashboardSmallGradeBinding.inflate(
                    LayoutInflater.from(context),
                    dashboardGradesSubitemGradeContainer,
                    false
                )

                with(subitemBinding.dashboardSmallGradeSubitemValue) {
                    text = it.entry
                    setBackgroundResource(it.getBackgroundColor(gradeColorTheme))
                }

                dashboardGradesSubitemGradeContainer.addView(subitemBinding.root)
            }
        }
    }

    class ViewHolder(val binding: SubitemDashboardGradesBinding) :
        RecyclerView.ViewHolder(binding.root)
}
