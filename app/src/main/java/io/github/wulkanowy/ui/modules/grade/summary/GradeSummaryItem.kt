package io.github.wulkanowy.ui.modules.grade.summary

import android.annotation.SuppressLint
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.GradeSummary
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_grade_summary.*

class GradeSummaryItem(
    val summary: GradeSummary,
    private val average: String
) : AbstractFlexibleItem<GradeSummaryItem.ViewHolder>() {

    override fun getLayoutRes() = R.layout.item_grade_summary

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>): ViewHolder {
        return ViewHolder(view, adapter)
    }

    @SuppressLint("SetTextI18n")
    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: ViewHolder, position: Int, payloads: MutableList<Any>?) {
        holder.run {
            gradeSummaryItemTitle.text = summary.subject
            gradeSummaryItemPoints.text = summary.pointsSum
            gradeSummaryItemAverage.text = average
            gradeSummaryItemPredicted.text = "${summary.predictedGrade} ${summary.proposedPoints}".trim()
            gradeSummaryItemFinal.text = "${summary.finalGrade} ${summary.finalPoints}".trim()

            gradeSummaryItemPointsContainer.visibility = if (summary.pointsSum.isBlank()) GONE else VISIBLE
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GradeSummaryItem

        if (average != other.average) return false
        if (summary != other.summary) return false
        if (summary.id != other.summary.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = summary.hashCode()
        result = 31 * result + summary.id.hashCode()
        result = 31 * result + average.hashCode()
        return result
    }

    class ViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter), LayoutContainer {
        override val containerView: View
            get() = contentView
    }
}
