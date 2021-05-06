package io.github.wulkanowy.ui.modules.exam

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.databinding.HeaderExamBinding
import io.github.wulkanowy.databinding.ItemExamBinding
import io.github.wulkanowy.utils.capitalise
import io.github.wulkanowy.utils.toFormattedString
import io.github.wulkanowy.utils.weekDayName
import java.time.LocalDate
import javax.inject.Inject

class ExamAdapter @Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items = emptyList<ExamItem<*>>()

    var onClickListener: (Exam) -> Unit = {}

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = items[position].viewType.id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            ExamItem.ViewType.HEADER.id -> HeaderViewHolder(HeaderExamBinding.inflate(inflater, parent, false))
            ExamItem.ViewType.ITEM.id -> ItemViewHolder(ItemExamBinding.inflate(inflater, parent, false))
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> bindHeaderViewHolder(holder.binding, items[position].value as LocalDate)
            is ItemViewHolder -> bindItemViewHolder(holder.binding, items[position].value as Exam)
        }
    }

    @SuppressLint("DefaultLocale")
    private fun bindHeaderViewHolder(binding: HeaderExamBinding, date: LocalDate) {
        with(binding) {
            examHeaderDay.text = date.weekDayName.capitalise()
            examHeaderDate.text = date.toFormattedString()
        }
    }

    private fun bindItemViewHolder(binding: ItemExamBinding, exam: Exam) {
        with(binding) {
            examItemSubject.text = exam.subject
            examItemTeacher.text = exam.teacher
            examItemType.text = exam.type

            root.setOnClickListener { onClickListener(exam) }
        }
    }

    private class HeaderViewHolder(val binding: HeaderExamBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class ItemViewHolder(val binding: ItemExamBinding) :
        RecyclerView.ViewHolder(binding.root)
}
