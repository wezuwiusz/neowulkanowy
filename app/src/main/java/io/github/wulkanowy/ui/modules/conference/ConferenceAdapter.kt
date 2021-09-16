package io.github.wulkanowy.ui.modules.conference

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.data.db.entities.Conference
import io.github.wulkanowy.databinding.ItemConferenceBinding
import io.github.wulkanowy.utils.toFormattedString
import javax.inject.Inject

class ConferenceAdapter @Inject constructor() :
    RecyclerView.Adapter<ConferenceAdapter.ItemViewHolder>() {

    var items = emptyList<Conference>()

    var onItemClickListener: (Conference) -> Unit = {}

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemConferenceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            conferenceItemDate.text = item.date.toFormattedString("dd.MM.yyyy HH:mm")
            conferenceItemName.text = item.presentOnConference
            conferenceItemTitle.text = item.title
            conferenceItemSubject.text = item.subject
            conferenceItemContent.text = item.agenda
            conferenceItemContent.visibility =
                if (item.agenda.isBlank()) View.GONE else View.VISIBLE

            root.setOnClickListener { onItemClickListener(item) }
        }
    }

    class ItemViewHolder(val binding: ItemConferenceBinding) : RecyclerView.ViewHolder(binding.root)
}
