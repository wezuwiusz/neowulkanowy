package io.github.wulkanowy.ui.modules.timetable.additional

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.data.db.entities.TimetableAdditional
import io.github.wulkanowy.databinding.ItemTimetableAdditionalBinding
import io.github.wulkanowy.utils.toFormattedString
import javax.inject.Inject

class AdditionalLessonsAdapter @Inject constructor() :
    RecyclerView.Adapter<AdditionalLessonsAdapter.ItemViewHolder>() {

    var items = emptyList<TimetableAdditional>()

    var onDeleteClickListener: (timetableAdditional: TimetableAdditional) -> Unit = {}

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemTimetableAdditionalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]

        with(holder.binding) {
            additionalLessonItemTime.text =
                "${item.start.toFormattedString("HH:mm")} - ${item.end.toFormattedString("HH:mm")}"
            additionalLessonItemSubject.text = item.subject

            additionalLessonItemDelete.isVisible = item.isAddedByUser
            additionalLessonItemDelete.setOnClickListener { onDeleteClickListener(item) }
        }
    }

    class ItemViewHolder(val binding: ItemTimetableAdditionalBinding) :
        RecyclerView.ViewHolder(binding.root)
}
