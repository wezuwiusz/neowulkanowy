package io.github.wulkanowy.ui.modules.grade.summary

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.GradeSummary
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.databinding.ItemGradeSummaryBinding
import io.github.wulkanowy.databinding.ScrollableHeaderGradeSummaryBinding
import io.github.wulkanowy.sdk.scrapper.grades.isGradeValid
import io.github.wulkanowy.utils.calcFinalAverage
import java.util.Locale
import javax.inject.Inject

class GradeSummaryAdapter @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private enum class ViewType(val id: Int) {
        HEADER(1),
        ITEM(2)
    }

    var items = emptyList<GradeSummary>()

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

        val context = binding.root.context
        val finalItemsCount = items.count { isGradeValid(it.finalGrade) }
        val calculatedItemsCount = items.count { value -> value.average != 0.0 }
        val allItemsCount = items.count { !it.subject.equals("zachowanie", true) }
        val finalAverage = items.calcFinalAverage(
            preferencesRepository.gradePlusModifier,
            preferencesRepository.gradeMinusModifier
        )
        val calculatedAverage = items.filter { value -> value.average != 0.0 }
            .map { values -> values.average }
            .reversed() // fix average precision
            .average()

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
    private fun bindItemViewHolder(binding: ItemGradeSummaryBinding, item: GradeSummary) {
        with(binding) {
            gradeSummaryItemTitle.text = item.subject
            gradeSummaryItemPoints.text = item.pointsSum
            gradeSummaryItemAverage.text = formatAverage(item.average, "")
            gradeSummaryItemPredicted.text = "${item.predictedGrade} ${item.proposedPoints}".trim()
            gradeSummaryItemFinal.text = "${item.finalGrade} ${item.finalPoints}".trim()

            gradeSummaryItemPointsContainer.visibility =
                if (item.pointsSum.isBlank()) View.GONE else View.VISIBLE
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
