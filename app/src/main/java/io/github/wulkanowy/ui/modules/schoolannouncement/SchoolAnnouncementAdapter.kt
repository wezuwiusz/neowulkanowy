package io.github.wulkanowy.ui.modules.schoolannouncement

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.data.db.entities.SchoolAnnouncement
import io.github.wulkanowy.databinding.ItemSchoolAnnouncementBinding
import io.github.wulkanowy.utils.toFormattedString
import javax.inject.Inject

class SchoolAnnouncementAdapter @Inject constructor() :
    RecyclerView.Adapter<SchoolAnnouncementAdapter.ViewHolder>() {

    var items = emptyList<SchoolAnnouncement>()

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemSchoolAnnouncementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        with(holder.binding) {
            schoolAnnouncementItemDate.text = item.date.toFormattedString()
            schoolAnnouncementItemType.text = item.subject
            schoolAnnouncementItemContent.text = HtmlCompat.fromHtml(
                item.content, HtmlCompat.FROM_HTML_MODE_COMPACT
            )
        }
    }

    class ViewHolder(val binding: ItemSchoolAnnouncementBinding) :
        RecyclerView.ViewHolder(binding.root)
}
