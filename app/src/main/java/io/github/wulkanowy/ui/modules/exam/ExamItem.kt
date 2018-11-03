package io.github.wulkanowy.ui.modules.exam

import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractSectionableItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Exam
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_exam.*

class ExamItem(header: ExamHeader, val exam: Exam) : AbstractSectionableItem<ExamItem.ViewHolder, ExamHeader>(header) {

    override fun getLayoutRes() = R.layout.item_exam

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: ViewHolder,
                                position: Int, payloads: MutableList<Any>?) {
        holder.run {
            examItemSubject.text = exam.subject
            examItemTeacher.text = exam.teacher
            examItemType.text = exam.type
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExamItem

        if (exam != other.exam) return false

        return true
    }

    override fun hashCode(): Int {
        return exam.hashCode()
    }

    class ViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter),
            LayoutContainer {

        override val containerView: View
            get() = contentView
    }
}
