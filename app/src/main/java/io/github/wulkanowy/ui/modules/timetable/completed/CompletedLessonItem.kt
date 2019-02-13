package io.github.wulkanowy.ui.modules.timetable.completed

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.CompletedLesson
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_completed_lesson.*

class CompletedLessonItem(val completedLesson: CompletedLesson) : AbstractFlexibleItem<CompletedLessonItem.ViewHolder>() {

    override fun getLayoutRes() = R.layout.item_completed_lesson

    override fun createViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<*>>?): CompletedLessonItem.ViewHolder {
        return CompletedLessonItem.ViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>?, holder: CompletedLessonItem.ViewHolder?, position: Int, payloads: MutableList<Any>?) {
        holder?.apply {
            completedLessonItemNumber.text = completedLesson.number.toString()
            completedLessonItemSubject.text = completedLesson.subject
            completedLessonItemTopic.text = completedLesson.topic
            completedLessonItemAlert.visibility = if (completedLesson.substitution.isNotEmpty()) VISIBLE else GONE
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CompletedLessonItem

        if (completedLesson != other.completedLesson) return false

        return true
    }

    override fun hashCode(): Int {
        return completedLesson.hashCode()
    }

    class ViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<*>>?) : FlexibleViewHolder(view, adapter),
        LayoutContainer {

        override val containerView: View?
            get() = contentView
    }
}
