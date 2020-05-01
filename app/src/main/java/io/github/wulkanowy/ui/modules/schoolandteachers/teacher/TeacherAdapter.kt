package io.github.wulkanowy.ui.modules.schoolandteachers.teacher

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Teacher
import io.github.wulkanowy.databinding.ItemTeacherBinding
import javax.inject.Inject

class TeacherAdapter @Inject constructor() : RecyclerView.Adapter<TeacherAdapter.ItemViewHolder>() {

    var items = emptyList<Teacher>()

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemTeacherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val teacher = items[position]

        with(holder.binding) {
            teacherItemName.text = teacher.name
            teacherItemSubject.text = if (teacher.subject.isNotBlank()) teacher.subject else root.context.getString(R.string.teacher_no_subject)
            if (teacher.shortName.isNotBlank()) {
                teacherItemShortName.visibility = View.VISIBLE
                teacherItemShortName.text = "[${teacher.shortName}]"
            } else {
                teacherItemShortName.visibility = View.GONE
            }
        }
    }

    class ItemViewHolder(val binding: ItemTeacherBinding) : RecyclerView.ViewHolder(binding.root)
}
