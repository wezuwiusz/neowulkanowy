package io.github.wulkanowy.ui.modules.timetable

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
import io.github.wulkanowy.utils.getThemeAttrColor
import io.github.wulkanowy.utils.toFormattedString
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_timetable.*
import kotlinx.android.synthetic.main.item_timetable_small.*

class TimetableItem(val lesson: Timetable, private val showWholeClassPlan: String) :
    AbstractFlexibleItem<TimetableItem.ViewHolder>() {

    override fun getLayoutRes() = when {
        showWholeClassPlan == "small" && !lesson.isStudentPlan -> R.layout.item_timetable_small
        else -> R.layout.item_timetable
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>): ViewHolder {
        return ViewHolder(view, adapter)
    }

    @SuppressLint("SetTextI18n")
    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: ViewHolder, position: Int, payloads: MutableList<Any>?) {
        when (itemViewType) {
            R.layout.item_timetable_small -> {
                with(holder) {
                    timetableSmallItemNumber.text = lesson.number.toString()
                    timetableSmallItemSubject.text = lesson.subject
                    timetableSmallItemTimeStart.text = lesson.start.toFormattedString("HH:mm")
                    timetableSmallItemRoom.text = lesson.room
                    timetableSmallItemTeacher.text = lesson.teacher
                }
            }
            R.layout.item_timetable -> {
                updateFields(holder)

                with(holder) {
                    timetableItemSubject.paintFlags =
                        if (lesson.canceled) timetableItemSubject.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        else timetableItemSubject.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }

                updateDescription(holder)
                updateColors(holder)
            }
        }
    }

    private fun updateFields(holder: ViewHolder) {
        with(holder) {
            timetableItemNumber.text = lesson.number.toString()
            timetableItemSubject.text = lesson.subject
            timetableItemRoom.text = lesson.room
            timetableItemTeacher.text = lesson.teacher
            timetableItemTimeStart.text = lesson.start.toFormattedString("HH:mm")
            timetableItemTimeFinish.text = lesson.end.toFormattedString("HH:mm")
        }
    }

    private fun updateDescription(holder: ViewHolder) {
        with(holder) {
            if (lesson.info.isNotBlank() && !lesson.changes) {
                updateDescriptionNoChanges(this)
            } else {
                timetableItemDescription.visibility = GONE

                timetableItemRoom.visibility = VISIBLE
                timetableItemTeacher.visibility = VISIBLE
            }
        }
    }

    private fun updateDescriptionNoChanges(holder: ViewHolder) {
        with(holder) {
            timetableItemDescription.visibility = VISIBLE
            timetableItemDescription.text = lesson.info

            timetableItemRoom.visibility = GONE
            timetableItemTeacher.visibility = GONE

            timetableItemDescription.setTextColor(holder.view.context.getThemeAttrColor(
                if (lesson.canceled) R.attr.colorPrimary
                else R.attr.colorTimetableChange
            ))
        }
    }

    private fun updateColors(holder: ViewHolder) {
        with(holder) {
            if (lesson.canceled) {
                timetableItemNumber.setTextColor(holder.view.context.getThemeAttrColor(R.attr.colorPrimary))
                timetableItemSubject.setTextColor(holder.view.context.getThemeAttrColor(R.attr.colorPrimary))
            } else {
                updateNumberColor(this)
                updateSubjectColor(this)
                updateRoomColor(this)
                updateTeacherColor(this)
            }
        }
    }

    private fun updateNumberColor(holder: ViewHolder) {
        holder.timetableItemNumber.setTextColor(holder.view.context.getThemeAttrColor(
            if (lesson.changes || lesson.info.isNotBlank()) R.attr.colorTimetableChange
            else android.R.attr.textColorPrimary
        ))
    }

    private fun updateSubjectColor(holder: ViewHolder) {
        holder.timetableItemSubject.setTextColor(holder.view.context.getThemeAttrColor(
            if (lesson.subjectOld.isNotBlank() && lesson.subjectOld != lesson.subject) R.attr.colorTimetableChange
            else android.R.attr.textColorPrimary
        ))
    }

    private fun updateRoomColor(holder: ViewHolder) {
        holder.timetableItemRoom.setTextColor(holder.view.context.getThemeAttrColor(
            if (lesson.roomOld.isNotBlank() && lesson.roomOld != lesson.room) R.attr.colorTimetableChange
            else android.R.attr.textColorSecondary
        ))
    }

    private fun updateTeacherColor(holder: ViewHolder) {
        holder.timetableItemTeacher.setTextColor(holder.view.context.getThemeAttrColor(
            if (lesson.teacherOld.isNotBlank() && lesson.teacherOld != lesson.teacher) R.attr.colorTimetableChange
            else android.R.attr.textColorSecondary
        ))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TimetableItem

        if (lesson != other.lesson) return false
        return true
    }

    override fun hashCode(): Int {
        var result = lesson.hashCode()
        result = 31 * result + lesson.id.toInt()
        return result
    }

    class ViewHolder(val view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter), LayoutContainer {
        override val containerView: View
            get() = contentView
    }
}
