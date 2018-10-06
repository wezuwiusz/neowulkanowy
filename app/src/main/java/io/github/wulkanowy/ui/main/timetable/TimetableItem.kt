package io.github.wulkanowy.ui.main.timetable

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.utils.toFormattedString
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_timetable.*

class TimetableItem : AbstractFlexibleItem<TimetableItem.ViewHolder>() {

    lateinit var lesson: Timetable

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun getLayoutRes(): Int = R.layout.item_timetable

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TimetableItem

        if (lesson != other.lesson) return false
        return true
    }

    override fun hashCode(): Int {
        return lesson.hashCode()
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: ViewHolder,
                                position: Int, payloads: MutableList<Any>?) {
        holder.bind(lesson)
    }

    class ViewHolder(val view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter),
            LayoutContainer {

        override val containerView: View
            get() = contentView

        @SuppressLint("SetTextI18n")
        fun bind(lesson: Timetable) {
            timetableItemNumber.text = lesson.number.toString()
            timetableItemSubject.text = lesson.subject
            timetableItemRoom.text = if (lesson.room.isNotBlank()) "${view.context.getString(R.string.timetable_room)} ${lesson.room}" else ""
            timetableItemTime.text = "${lesson.start.toFormattedString("HH:mm")} - ${lesson.end.toFormattedString("HH:mm")}"
            timetableItemAlert.visibility = if (lesson.changes || lesson.canceled) VISIBLE else GONE
            timetableItemSubject.paintFlags =
                    if (lesson.canceled) timetableItemSubject.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    else timetableItemSubject.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }
}
