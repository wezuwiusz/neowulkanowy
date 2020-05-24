package io.github.wulkanowy.ui.modules.grade.details

import android.annotation.SuppressLint
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.databinding.HeaderGradeDetailsBinding
import io.github.wulkanowy.databinding.ItemGradeDetailsBinding
import io.github.wulkanowy.ui.base.BaseExpandableAdapter
import io.github.wulkanowy.utils.getBackgroundColor
import io.github.wulkanowy.utils.toFormattedString
import timber.log.Timber
import javax.inject.Inject

class GradeDetailsAdapter @Inject constructor() : BaseExpandableAdapter<RecyclerView.ViewHolder>() {

    private var headers = mutableListOf<GradeDetailsItem>()

    private var items = mutableListOf<GradeDetailsItem>()

    private var expandedPosition = RecyclerView.NO_POSITION

    private var isExpandable = false

    var onClickListener: (Grade, position: Int) -> Unit = { _, _ -> }

    var colorTheme = ""

    fun setDataItems(data: List<GradeDetailsItem>, isExpanded: Boolean = isExpandable) {
        headers = data.filter { it.viewType == ViewType.HEADER }.toMutableList()
        items = if (isExpanded) headers else data.toMutableList()
        isExpandable = isExpanded
        expandedPosition = RecyclerView.NO_POSITION
    }

    fun updateDetailsItem(position: Int, grade: Grade) {
        if (items.getOrNull(position)?.viewType != ViewType.ITEM) {
            Timber.e("Trying to update item $position on list ${items.size} size, expanded position: $expandedPosition")
            return
        }
        items[position] = GradeDetailsItem(grade, ViewType.ITEM)
        notifyItemChanged(position)
    }

    fun getHeaderItem(subject: String): GradeDetailsItem {
        if (headers.any { it.value !is GradeDetailsHeader }) {
            Timber.e("Headers contains no-header items! $headers")
        }

        val candidates = headers.filter { (it.value as GradeDetailsHeader).subject == subject }

        if (candidates.size > 1) {
            Timber.e("Header with subject $subject found ${candidates.size} times! Items: $candidates, expanded: $expandedPosition")
        }

        return candidates.first()
    }

    fun updateHeaderItem(item: GradeDetailsItem) {
        headers[headers.indexOf(item)] = item
        items[items.indexOf(item)] = item
        notifyItemChanged(items.indexOf(item))
    }

    fun collapseAll() {
        if (expandedPosition != -1) {
            refreshList(headers)
            expandedPosition = RecyclerView.NO_POSITION
        }
    }

    @Synchronized
    private fun refreshList(newItems: List<GradeDetailsItem>) {
        val diffCallback = GradeDetailsDiffUtil(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = newItems.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = items[position].viewType.id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            ViewType.HEADER.id -> HeaderViewHolder(HeaderGradeDetailsBinding.inflate(inflater, parent, false))
            ViewType.ITEM.id -> ItemViewHolder(ItemGradeDetailsBinding.inflate(inflater, parent, false))
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> bindHeaderViewHolder(
                binding = holder.binding,
                header = items[position].value as GradeDetailsHeader,
                headerPosition = headers.indexOf(items[position]),
                adapterPosition = position
            )
            is ItemViewHolder -> bindItemViewHolder(
                binding = holder.binding,
                grade = items[position].value as Grade,
                position = holder.adapterPosition
            )
        }
    }

    private fun bindHeaderViewHolder(binding: HeaderGradeDetailsBinding, header: GradeDetailsHeader, headerPosition: Int, adapterPosition: Int) {
        with(binding) {
            gradeHeaderDivider.visibility = if (adapterPosition == 0) View.GONE else View.VISIBLE
            gradeHeaderSubject.apply {
                text = header.subject
                maxLines = if (headerPosition == expandedPosition) 2 else 1
            }
            gradeHeaderAverage.text = formatAverage(header.average, root.context.resources)
            gradeHeaderPointsSum.text = root.context.getString(R.string.grade_points_sum, header.pointsSum)
            gradeHeaderPointsSum.visibility = if (!header.pointsSum.isNullOrEmpty()) View.VISIBLE else View.GONE
            gradeHeaderNumber.text = root.context.resources.getQuantityString(R.plurals.grade_number_item, header.grades.size, header.grades.size)
            gradeHeaderNote.visibility = if (header.newGrades > 0) View.VISIBLE else View.GONE
            if (header.newGrades > 0) gradeHeaderNote.text = header.newGrades.toString(10)

            gradeHeaderContainer.isEnabled = isExpandable
            gradeHeaderContainer.setOnClickListener {
                expandedPosition = if (expandedPosition == adapterPosition) -1 else adapterPosition

                if (expandedPosition != RecyclerView.NO_POSITION) {
                    refreshList(headers.toMutableList().apply {
                        addAll(headerPosition + 1, header.grades)
                    })
                    scrollToHeaderWithSubItems(headerPosition, header.grades.size)
                } else {
                    refreshList(headers)
                }
            }
        }
    }

    private fun formatAverage(average: Double?, resources: Resources): String {
        return if (average == null || average == .0) resources.getString(R.string.grade_no_average)
        else resources.getString(R.string.grade_average, average)
    }

    @SuppressLint("SetTextI18n")
    private fun bindItemViewHolder(binding: ItemGradeDetailsBinding, grade: Grade, position: Int) {
        with(binding) {
            gradeItemValue.run {
                text = grade.entry
                setBackgroundResource(grade.getBackgroundColor(colorTheme))
            }
            gradeItemDescription.text = when {
                grade.description.isNotBlank() -> grade.description
                grade.gradeSymbol.isNotBlank() -> grade.gradeSymbol
                else -> root.context.getString(R.string.all_no_description)
            }
            gradeItemDate.text = grade.date.toFormattedString()
            gradeItemWeight.text = "${root.context.getString(R.string.grade_weight)}: ${grade.weight}"
            gradeItemNote.visibility = if (!grade.isRead) View.VISIBLE else View.GONE

            root.setOnClickListener { onClickListener(grade, position) }
        }
    }

    private class HeaderViewHolder(val binding: HeaderGradeDetailsBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class ItemViewHolder(val binding: ItemGradeDetailsBinding) :
        RecyclerView.ViewHolder(binding.root)

    class GradeDetailsDiffUtil(private val old: List<GradeDetailsItem>, private val new: List<GradeDetailsItem>) :
        DiffUtil.Callback() {

        override fun getOldListSize() = old.size

        override fun getNewListSize() = new.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return old[oldItemPosition] == new[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return old[oldItemPosition] == new[newItemPosition]
        }
    }
}
