package io.github.wulkanowy.ui.modules.grade.details

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
    private val pointsSum: String,
    var newGrades: Int,
    private val isExpandable: Boolean
) : AbstractExpandableItem<GradeDetailsHeader.ViewHolder, GradeDetailsItem>() {

    init {
        isExpanded = !isExpandable
    }

    override fun getLayoutRes() = R.layout.header_grade_details

    override fun createViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<*>>?): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>?, holder: ViewHolder, position: Int, payloads: MutableList<Any>?) {
        holder.run {
            gradeHeaderSubject.apply {
                text = subject
                maxLines = if (isExpanded) 2 else 1
            }
            gradeHeaderAverage.text = average
            gradeHeaderPointsSum.text = pointsSum
            gradeHeaderPointsSum.visibility = if (pointsSum.isNotEmpty()) VISIBLE else GONE
            gradeHeaderNumber.text = number
            gradeHeaderNote.visibility = if (newGrades > 0) VISIBLE else GONE
            if (newGrades > 0) gradeHeaderNote.text = newGrades.toString(10)
            gradeHeaderContainer.isEnabled = isExpandable

            isViewExpandable = isExpandable
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GradeDetailsHeader

        if (subject != other.subject) return false
        if (number != other.number) return false
        if (average != other.average) return false
        if (isExpandable != other.isExpandable) return false

        return true
    }

    override fun hashCode(): Int {
        var result = subject.hashCode()
        result = 31 * result + number.hashCode()
        result = 31 * result + average.hashCode()
        result = 31 * result + isExpandable.hashCode()
        return result
    }

    class ViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<*>>?) :
        ExpandableViewHolder(view, adapter), LayoutContainer {

        var isViewExpandable = true

        init {
            contentView.setOnClickListener(this)
        }

        override val containerView: View
            get() = contentView

        override fun isViewCollapsibleOnClick() = isViewExpandable

        override fun isViewExpandableOnClick() = isViewExpandable

        override fun onClick(view: View?) {
            super.onClick(view)
            mAdapter.getItem(adapterPosition)?.let { mAdapter.updateItem(it) }
        }
    }
}
