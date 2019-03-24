package io.github.wulkanowy.ui.modules.homework

import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractSectionableItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Homework
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_homework.*

class HomeworkItem(header: HomeworkHeader, val homework: Homework) :
    AbstractSectionableItem<HomeworkItem.ViewHolder, HomeworkHeader>(header) {

    override fun getLayoutRes() = R.layout.item_homework

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: ViewHolder, position: Int, payloads: MutableList<Any>?) {
        holder.apply {
            homeworkItemSubject.text = homework.subject
            homeworkItemTeacher.text = homework.teacher
            homeworkItemContent.text = homework.content
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HomeworkItem

        if (homework != other.homework) return false
        return true
    }

    override fun hashCode(): Int {
        var result = homework.hashCode()
        result = 31 * result + homework.id.toInt()
        return result
    }

    class ViewHolder(val view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter), LayoutContainer {
        override val containerView: View
            get() = contentView
    }
}
