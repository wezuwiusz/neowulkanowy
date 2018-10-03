package io.github.wulkanowy.ui.main.grade.details

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractExpandableItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.ExpandableViewHolder
import io.github.wulkanowy.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.header_grade_details.*

class GradeDetailsHeader(
        private val subject: String,
        private val number: String,
        private val average: String,
        var newGrades: Int)
    : AbstractExpandableItem<GradeDetailsHeader.ViewHolder, GradeDetailsItem>() {

    override fun getLayoutRes() = R.layout.header_grade_details

    override fun createViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<*>>?): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>?, holder: ViewHolder,
                                position: Int, payloads: MutableList<Any>?) {
        holder.run {
            gradeHeaderSubject.text = subject
            gradeHeaderAverage.text = average
            gradeHeaderNumber.text = number
            gradeHeaderPredicted.visibility = GONE
            gradeHeaderFinal.visibility = GONE

            gradeHeaderNote.visibility = if (newGrades > 0) VISIBLE else GONE
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GradeDetailsHeader

        if (subject != other.subject) return false
        if (number != other.number) return false
        if (average != other.average) return false

        return true
    }

    override fun hashCode(): Int {
        var result = subject.hashCode()
        result = 31 * result + number.hashCode()
        result = 31 * result + average.hashCode()
        return result
    }


    class ViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<*>>?) : ExpandableViewHolder(view, adapter),
            LayoutContainer {

        init {
            contentView.setOnClickListener(this)
        }

        override fun shouldNotifyParentOnClick() = true

        override val containerView: View
            get() = contentView
    }
}
