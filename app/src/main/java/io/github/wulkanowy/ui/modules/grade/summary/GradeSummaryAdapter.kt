package io.github.wulkanowy.ui.modules.grade.summary

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.R
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.databinding.ItemGradeSummaryBinding
import io.github.wulkanowy.databinding.ScrollableHeaderGradeSummaryBinding
import io.github.wulkanowy.sdk.scrapper.grades.isGradeValid
import io.github.wulkanowy.utils.calcFinalAverage
import io.github.wulkanowy.utils.ifNullOrBlank
import java.util.Locale
import javax.inject.Inject

class GradeSummaryAdapter @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private enum class ViewType(val id: Int) {
        HEADER(1),
        ITEM(2)
    }

    var items = emptyList<GradeSummaryItem>()

    var onCalculatedHelpClickListener: () -> Unit = {}

    var onFinalHelpClickListener: () -> Unit = {}

    override fun getItemCount() = items.size + if (items.isNotEmpty()) 1 else 0

    override fun getItemViewType(position: Int) = when (position) {
        0 -> ViewType.HEADER.id
        else -> ViewType.ITEM.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            ViewType.HEADER.id -> HeaderViewHolder(
                ScrollableHeaderGradeSummaryBinding.inflate(inflater, parent, false)
            )

            ViewType.ITEM.id -> ItemViewHolder(
                ItemGradeSummaryBinding.inflate(inflater, parent, false)
            )

            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> bindHeaderViewHolder(holder.binding)
            is ItemViewHolder -> bindItemViewHolder(holder.binding, items[position - 1])
        }
    }

    private fun bindHeaderViewHolder(binding: ScrollableHeaderGradeSummaryBinding) {
        if (items.isEmpty()) return
        val gradeSummaries = items
            .filter { it.gradeDescriptive == null }
            .map { it.gradeSummary }

        val context = binding.root.context
        val finalItemsCount = gradeSummaries.count { isGradeValid(it.finalGrade) }
        val calculatedItemsCount = gradeSummaries.count { value -> value.average != 0.0 }
        val allItemsCount = gradeSummaries.count { !it.subject.equals("zachowanie", true) }
        val finalAverage = gradeSummaries.calcFinalAverage(
            preferencesRepository.gradePlusModifier,
            preferencesRepository.gradeMinusModifier
        )
        val calculatedAverage = gradeSummaries.filter { value -> value.average != 0.0 }
            .map { values -> values.average }
            .reversed() // fix average precision
            .average()
            .let { if (it.isNaN()) 0.0 else it }

        with(binding) {
            gradeSummaryScrollableHeaderFinal.text = formatAverage(finalAverage)
            gradeSummaryScrollableHeaderCalculated.text = formatAverage(calculatedAverage)
            gradeSummaryScrollableHeaderFinalSubjectCount.text =
                context.getString(
                    R.string.grade_summary_from_subjects,
                    finalItemsCount,
                    allItemsCount
                )
            gradeSummaryScrollableHeaderCalculatedSubjectCount.text = context.getString(
                R.string.grade_summary_from_subjects,
                calculatedItemsCount,
                allItemsCount
            )

            gradeSummaryCalculatedAverageHelp.setOnClickListener { onCalculatedHelpClickListener() }
            gradeSummaryFinalAverageHelp.setOnClickListener { onFinalHelpClickListener() }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindItemViewHolder(binding: ItemGradeSummaryBinding, item: GradeSummaryItem) {
        val (gradeSummary, gradeDescriptive) = item

        with(binding) {
            gradeSummaryItemTitle.text = gradeSummary.subject
            gradeSummaryItemPoints.text = gradeSummary.pointsSum
            gradeSummaryItemAverage.text = formatAverage(gradeSummary.average, "")
            gradeSummaryItemPredicted.text =
                "${gradeSummary.predictedGrade} ${gradeSummary.proposedPoints}".trim()
            gradeSummaryItemFinal.text =
                "${gradeSummary.finalGrade} ${gradeSummary.finalPoints}".trim()
            gradeSummaryItemDescriptive.text = gradeDescriptive?.description.ifNullOrBlank {
                root.context.getString(R.string.all_no_data)
            }

            gradeSummaryItemFinalDivider.isVisible = gradeDescriptive == null
            gradeSummaryItemPredictedDivider.isVisible = gradeDescriptive == null
            gradeSummaryItemPointsDivider.isVisible = gradeDescriptive == null
            gradeSummaryItemPredictedContainer.isVisible = gradeDescriptive == null
            gradeSummaryItemFinalContainer.isVisible = gradeDescriptive == null
            gradeSummaryItemDescriptiveContainer.isVisible = gradeDescriptive != null
            gradeSummaryItemPointsContainer.isVisible = gradeSummary.pointsSum.isNotBlank()
        }
    }

    private fun formatAverage(average: Double, defaultValue: String = "-- --"): String {
        return if (average == 0.0) defaultValue
        else String.format(Locale.FRANCE, "%.2f", average)
    }

    private class HeaderViewHolder(val binding: ScrollableHeaderGradeSummaryBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class ItemViewHolder(val binding: ItemGradeSummaryBinding) :
        RecyclerView.ViewHolder(binding.root)
}
