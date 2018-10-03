package io.github.wulkanowy.ui.main.grade.summary

import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.github.wulkanowy.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.scrollable_header_grade_summary.*

class GradeSummaryScrollableHeader(private val finalAverage: String, private val calculatedAverage: String)
    : AbstractFlexibleItem<GradeSummaryScrollableHeader.ViewHolder>() {

    override fun getLayoutRes() = R.layout.scrollable_header_grade_summary

    override fun createViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<*>>?): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>?, holder: ViewHolder?,
                                position: Int, payloads: MutableList<Any>?) {
        holder?.apply {
            gradeSummaryScrollableHeaderFinal.text = finalAverage
            gradeSummaryScrollableHeaderCalculated.text = calculatedAverage
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GradeSummaryScrollableHeader

        if (calculatedAverage != other.calculatedAverage) return false
        if (finalAverage != other.finalAverage) return false

        return true
    }

    override fun hashCode(): Int {
        var result = calculatedAverage.hashCode()
        result = 31 * result + finalAverage.hashCode()
        return result
    }

    class ViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<*>>?) : FlexibleViewHolder(view, adapter),
            LayoutContainer {

        override val containerView: View?
            get() = contentView
    }
}
