package io.github.wulkanowy.ui.modules.timetable.completed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.CompletedLesson
import io.github.wulkanowy.databinding.ItemCompletedLessonBinding
import io.github.wulkanowy.utils.getThemeAttrColor
import javax.inject.Inject

class CompletedLessonsAdapter @Inject constructor() :
    RecyclerView.Adapter<CompletedLessonsAdapter.ItemViewHolder>() {

    var items = emptyList<CompletedLesson>()

    var onClickListener: (CompletedLesson) -> Unit = {}

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemCompletedLessonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]

        with(holder.binding) {
            completedLessonItemNumber.text = item.number.toString()
            completedLessonItemNumber.setTextColor(root.context.getThemeAttrColor(
                if (item.substitution.isNotEmpty()) R.attr.colorTimetableChange
                else android.R.attr.textColorPrimary
            ))
            completedLessonItemSubject.text = item.subject
            completedLessonItemTopic.text = item.topic
            completedLessonItemAlert.visibility = if (item.substitution.isNotEmpty()) View.VISIBLE else View.GONE

            root.setOnClickListener { onClickListener(item) }
        }
    }

    class ItemViewHolder(val binding: ItemCompletedLessonBinding) :
        RecyclerView.ViewHolder(binding.root)
}
