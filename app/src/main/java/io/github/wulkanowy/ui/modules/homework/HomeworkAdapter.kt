package io.github.wulkanowy.ui.modules.homework

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.databinding.HeaderHomeworkBinding
import io.github.wulkanowy.databinding.ItemHomeworkBinding
import io.github.wulkanowy.utils.capitalise
import io.github.wulkanowy.utils.toFormattedString
import io.github.wulkanowy.utils.weekDayName
import java.time.LocalDate
import javax.inject.Inject

class HomeworkAdapter @Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items = emptyList<HomeworkItem<*>>()

    var onClickListener: (Homework) -> Unit = {}

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = items[position].viewType.id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            HomeworkItem.ViewType.HEADER.id -> HeaderViewHolder(HeaderHomeworkBinding.inflate(inflater, parent, false))
            HomeworkItem.ViewType.ITEM.id -> ItemViewHolder(ItemHomeworkBinding.inflate(inflater, parent, false))
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> bindHeaderViewHolder(holder.binding, items[position].value as LocalDate)
            is ItemViewHolder -> bindItemViewHolder(holder.binding, items[position].value as Homework)
        }
    }

    @SuppressLint("DefaultLocale")
    private fun bindHeaderViewHolder(binding: HeaderHomeworkBinding, date: LocalDate) {
        with(binding) {
            homeworkHeaderDay.text = date.weekDayName.capitalise()
            homeworkHeaderDate.text = date.toFormattedString()
        }
    }

    private fun bindItemViewHolder(binding: ItemHomeworkBinding, homework: Homework) {
        with(binding) {
            homeworkItemSubject.text = homework.subject
            homeworkItemTeacher.text = homework.teacher
            homeworkItemContent.text = homework.content
            homeworkItemCheckImage.visibility = if (homework.isDone) View.VISIBLE else View.GONE
            homeworkItemAttachmentImage.visibility = if (!homework.isDone && homework.attachments.isNotEmpty()) View.VISIBLE else View.GONE

            root.setOnClickListener { onClickListener(homework) }
        }
    }

    class HeaderViewHolder(val binding: HeaderHomeworkBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ItemViewHolder(val binding: ItemHomeworkBinding) : RecyclerView.ViewHolder(binding.root)
}
