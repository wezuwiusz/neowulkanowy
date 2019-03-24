package io.github.wulkanowy.ui.modules.grade.summary

import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.github.wulkanowy.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_grade_summary.*

class GradeSummaryItem(
    private val title: String,
    private val average: String,
    private val predictedGrade: String,
    private val finalGrade: String
) : AbstractFlexibleItem<GradeSummaryItem.ViewHolder>() {

    override fun getLayoutRes() = R.layout.item_grade_summary

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: ViewHolder, position: Int, payloads: MutableList<Any>?) {
        holder.run {
            gradeSummaryItemTitle.text = title
            gradeSummaryItemAverage.text = average
            gradeSummaryItemPredicted.text = predictedGrade
            gradeSummaryItemFinal.text = finalGrade
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GradeSummaryItem

        if (average != other.average) return false
        if (title != other.title) return false
        if (predictedGrade != other.predictedGrade) return false
        if (finalGrade != other.finalGrade) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + average.hashCode()
        result = 31 * result + predictedGrade.hashCode()
        result = 31 * result + finalGrade.hashCode()
        return result
    }

    class ViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter), LayoutContainer {
        override val containerView: View
            get() = contentView
    }
}
