package io.github.wulkanowy.ui.modules.timetable

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
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
            R.layout.item_timetable_small -> bindSmallView(holder)
            R.layout.item_timetable -> bindNormalView(holder)
        }
    }

    private fun bindSmallView(holder: ViewHolder) {
        with(holder) {
            timetableSmallItemNumber.text = lesson.number.toString()
            timetableSmallItemSubject.text = lesson.subject
            timetableSmallItemTimeStart.text = lesson.start.toFormattedString("HH:mm")
            timetableSmallItemRoom.text = lesson.room
            timetableSmallItemTeacher.text = lesson.teacher

            updateSubjectStyle(timetableSmallItemSubject)
            updateSmallDescription(this)
            updateSmallColors(this)
        }
    }

    private fun bindNormalView(holder: ViewHolder) {
        with(holder) {
            timetableItemNumber.text = lesson.number.toString()
            timetableItemSubject.text = lesson.subject
            timetableItemRoom.text = lesson.room
            timetableItemTeacher.text = lesson.teacher
            timetableItemTimeStart.text = lesson.start.toFormattedString("HH:mm")
            timetableItemTimeFinish.text = lesson.end.toFormattedString("HH:mm")

            updateSubjectStyle(timetableItemSubject)
            updateNormalDescription(this)
            updateNormalColors(this)
        }
    }

    private fun updateSubjectStyle(subjectView: TextView) {
        subjectView.paintFlags = if (lesson.canceled) subjectView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        else subjectView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }

    private fun updateSmallDescription(holder: ViewHolder) {
        with(holder) {
            if (lesson.info.isNotBlank() && !lesson.changes) {
                timetableSmallItemDescription.visibility = VISIBLE
                timetableSmallItemDescription.text = lesson.info

                timetableSmallItemRoom.visibility = GONE
                timetableSmallItemTeacher.visibility = GONE

                timetableSmallItemDescription.setTextColor(holder.view.context.getThemeAttrColor(
                    if (lesson.canceled) R.attr.colorPrimary
                    else R.attr.colorTimetableChange
                ))
            } else {
                timetableSmallItemDescription.visibility = GONE
                timetableSmallItemRoom.visibility = VISIBLE
                timetableSmallItemTeacher.visibility = VISIBLE
            }
        }
    }

    private fun updateNormalDescription(holder: ViewHolder) {
        with(holder) {
            if (lesson.info.isNotBlank() && !lesson.changes) {
                timetableItemDescription.visibility = VISIBLE
                timetableItemDescription.text = lesson.info

                timetableItemRoom.visibility = GONE
                timetableItemTeacher.visibility = GONE

                timetableItemDescription.setTextColor(holder.view.context.getThemeAttrColor(
                    if (lesson.canceled) R.attr.colorPrimary
                    else R.attr.colorTimetableChange
                ))
            } else {
                timetableItemDescription.visibility = GONE
                timetableItemRoom.visibility = VISIBLE
                timetableItemTeacher.visibility = VISIBLE
            }
        }
    }

    private fun updateSmallColors(holder: ViewHolder) {
        with(holder) {
            if (lesson.canceled) {
                updateNumberAndSubjectCanceledColor(timetableSmallItemNumber, timetableSmallItemSubject)
            } else {
                updateNumberColor(timetableSmallItemNumber)
                updateSubjectColor(timetableSmallItemSubject)
                updateRoomColor(timetableSmallItemRoom)
                updateTeacherColor(timetableSmallItemTeacher)
            }
        }
    }

    private fun updateNormalColors(holder: ViewHolder) {
        with(holder) {
            if (lesson.canceled) {
                updateNumberAndSubjectCanceledColor(timetableItemNumber, timetableItemSubject)
            } else {
                updateNumberColor(timetableItemNumber)
                updateSubjectColor(timetableItemSubject)
                updateRoomColor(timetableItemRoom)
                updateTeacherColor(timetableItemTeacher)
            }
        }
    }

    private fun updateNumberAndSubjectCanceledColor(numberView: TextView, subjectView: TextView) {
        numberView.setTextColor(numberView.context.getThemeAttrColor(R.attr.colorPrimary))
        subjectView.setTextColor(subjectView.context.getThemeAttrColor(R.attr.colorPrimary))
    }

    private fun updateNumberColor(numberView: TextView) {
        numberView.setTextColor(numberView.context.getThemeAttrColor(
            if (lesson.changes || lesson.info.isNotBlank()) R.attr.colorTimetableChange
            else android.R.attr.textColorPrimary
        ))
    }

    private fun updateSubjectColor(subjectView: TextView) {
        subjectView.setTextColor(subjectView.context.getThemeAttrColor(
            if (lesson.subjectOld.isNotBlank() && lesson.subjectOld != lesson.subject) R.attr.colorTimetableChange
            else android.R.attr.textColorPrimary
        ))
    }

    private fun updateRoomColor(roomView: TextView) {
        roomView.setTextColor(roomView.context.getThemeAttrColor(
            if (lesson.roomOld.isNotBlank() && lesson.roomOld != lesson.room) R.attr.colorTimetableChange
            else android.R.attr.textColorSecondary
        ))
    }

    private fun updateTeacherColor(teacherTextView: TextView) {
        teacherTextView.setTextColor(teacherTextView.context.getThemeAttrColor(
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

    class ViewHolder(val view: View, adapter: FlexibleAdapter<*>) :
        FlexibleViewHolder(view, adapter), LayoutContainer {
        override val containerView: View
            get() = contentView
    }
}
