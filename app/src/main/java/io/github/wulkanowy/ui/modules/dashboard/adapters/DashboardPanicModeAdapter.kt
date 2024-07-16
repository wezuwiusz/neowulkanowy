package io.github.wulkanowy.ui.modules.dashboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.data.db.entities.Conference
import io.github.wulkanowy.databinding.FragmentPanicModeBinding
import io.github.wulkanowy.utils.toFormattedString

class DashboardPanicModeAdapter :
    RecyclerView.Adapter<DashboardPanicModeAdapter.ViewHolder>() {

    override fun getItemCount() = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        FragmentPanicModeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //
    }

    class ViewHolder(val binding: FragmentPanicModeBinding) :
        RecyclerView.ViewHolder(binding.root)
}
