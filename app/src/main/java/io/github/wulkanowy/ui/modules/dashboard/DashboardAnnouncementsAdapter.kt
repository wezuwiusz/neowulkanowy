package io.github.wulkanowy.ui.modules.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.data.db.entities.SchoolAnnouncement
import io.github.wulkanowy.databinding.SubitemDashboardAnnouncementsBinding
import io.github.wulkanowy.utils.toFormattedString

class DashboardAnnouncementsAdapter :
    RecyclerView.Adapter<DashboardAnnouncementsAdapter.ViewHolder>() {

    var items = emptyList<SchoolAnnouncement>()

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        SubitemDashboardAnnouncementsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        with(holder.binding) {
            dashboardHomeworkSubitemTime.text = item.date.toFormattedString()
            dashboardHomeworkSubitemTitle.text = item.subject
        }
    }

    class ViewHolder(val binding: SubitemDashboardAnnouncementsBinding) :
        RecyclerView.ViewHolder(binding.root)
}