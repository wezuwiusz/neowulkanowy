package io.github.wulkanowy.ui.modules.timetable

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.databinding.ItemTimetableBinding
import io.github.wulkanowy.databinding.ItemTimetableSmallBinding
import io.github.wulkanowy.utils.getThemeAttrColor
import io.github.wulkanowy.utils.toFormattedString
import javax.inject.Inject

class TimetableAdapter @Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private enum class ViewType(val id: Int) {
        ITEM_NORMAL(1),
        ITEM_SMALL(2)
    }

    var items = emptyList<Timetable>()

    var onClickListener: (Timetable) -> Unit = {}

    var showWholeClassPlan: String = "no"

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = when {
        !items[position].isStudentPlan && showWholeClassPlan == "small" -> ViewType.ITEM_SMALL.id
        else -> ViewType.ITEM_NORMAL.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            ViewType.ITEM_NORMAL.id -> ItemViewHolder(ItemTimetableBinding.inflate(inflater, parent, false))
            ViewType.ITEM_SMALL.id -> SmallItemViewHolder(ItemTimetableSmallBinding.inflate(inflater, parent, false))
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val lesson = items[position]

        when (holder) {
            is ItemViewHolder -> bindNormalView(holder.binding, lesson)
            is SmallItemViewHolder -> bindSmallView(holder.binding, lesson)
        }
    }

    private fun bindSmallView(binding: ItemTimetableSmallBinding, lesson: Timetable) {
        with(binding) {
            timetableSmallItemNumber.text = lesson.number.toString()
            timetableSmallItemSubject.text = lesson.subject
            timetableSmallItemTimeStart.text = lesson.start.toFormattedString("HH:mm")
            timetableSmallItemRoom.text = lesson.room
            timetableSmallItemTeacher.text = lesson.teacher

            bindSubjectStyle(timetableSmallItemSubject, lesson)
            bindSmallDescription(binding, lesson)
            bindSmallColors(binding, lesson)

            root.setOnClickListener { onClickListener(lesson) }
        }
    }

    private fun bindNormalView(binding: ItemTimetableBinding, lesson: Timetable) {
        with(binding) {
            timetableItemNumber.text = lesson.number.toString()
            timetableItemSubject.text = lesson.subject
            timetableItemRoom.text = lesson.room
            timetableItemTeacher.text = lesson.teacher
            timetableItemTimeStart.text = lesson.start.toFormattedString("HH:mm")
            timetableItemTimeFinish.text = lesson.end.toFormattedString("HH:mm")

            bindSubjectStyle(timetableItemSubject, lesson)
            bindNormalDescription(binding, lesson)
            bindNormalColors(binding, lesson)

            root.setOnClickListener { onClickListener(lesson) }
        }
    }

    private fun bindSubjectStyle(subjectView: TextView, lesson: Timetable) {
        subjectView.paintFlags = if (lesson.canceled) subjectView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        else subjectView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }

    private fun bindSmallDescription(binding: ItemTimetableSmallBinding, lesson: Timetable) {
        with(binding) {
            if (lesson.info.isNotBlank() && !lesson.changes) {
                timetableSmallItemDescription.visibility = android.view.View.VISIBLE
                timetableSmallItemDescription.text = lesson.info

                timetableSmallItemRoom.visibility = android.view.View.GONE
                timetableSmallItemTeacher.visibility = android.view.View.GONE

                timetableSmallItemDescription.setTextColor(root.context.getThemeAttrColor(
                    if (lesson.canceled) R.attr.colorPrimary
                    else R.attr.colorTimetableChange
                ))
            } else {
                timetableSmallItemDescription.visibility = android.view.View.GONE
                timetableSmallItemRoom.visibility = android.view.View.VISIBLE
                timetableSmallItemTeacher.visibility = android.view.View.VISIBLE
            }
        }
    }

    private fun bindNormalDescription(binding: ItemTimetableBinding, lesson: Timetable) {
        with(binding) {
            if (lesson.info.isNotBlank() && !lesson.changes) {
                timetableItemDescription.visibility = android.view.View.VISIBLE
                timetableItemDescription.text = lesson.info

                timetableItemRoom.visibility = android.view.View.GONE
                timetableItemTeacher.visibility = android.view.View.GONE

                timetableItemDescription.setTextColor(root.context.getThemeAttrColor(
                    if (lesson.canceled) R.attr.colorPrimary
                    else R.attr.colorTimetableChange
                ))
            } else {
                timetableItemDescription.visibility = android.view.View.GONE
                timetableItemRoom.visibility = android.view.View.VISIBLE
                timetableItemTeacher.visibility = android.view.View.VISIBLE
            }
        }
    }

    private fun bindSmallColors(binding: ItemTimetableSmallBinding, lesson: Timetable) {
        with(binding) {
            if (lesson.canceled) {
                updateNumberAndSubjectCanceledColor(timetableSmallItemNumber, timetableSmallItemSubject)
            } else {
                updateNumberColor(timetableSmallItemNumber, lesson)
                updateSubjectColor(timetableSmallItemSubject, lesson)
                updateRoomColor(timetableSmallItemRoom, lesson)
                updateTeacherColor(timetableSmallItemTeacher, lesson)
            }
        }
    }

    private fun bindNormalColors(binding: ItemTimetableBinding, lesson: Timetable) {
        with(binding) {
            if (lesson.canceled) {
                updateNumberAndSubjectCanceledColor(timetableItemNumber, timetableItemSubject)
            } else {
                updateNumberColor(timetableItemNumber, lesson)
                updateSubjectColor(timetableItemSubject, lesson)
                updateRoomColor(timetableItemRoom, lesson)
                updateTeacherColor(timetableItemTeacher, lesson)
            }
        }
    }

    private fun updateNumberAndSubjectCanceledColor(numberView: TextView, subjectView: TextView) {
        numberView.setTextColor(numberView.context.getThemeAttrColor(R.attr.colorPrimary))
        subjectView.setTextColor(subjectView.context.getThemeAttrColor(R.attr.colorPrimary))
    }

    private fun updateNumberColor(numberView: TextView, lesson: Timetable) {
        numberView.setTextColor(numberView.context.getThemeAttrColor(
            if (lesson.changes || lesson.info.isNotBlank()) R.attr.colorTimetableChange
            else android.R.attr.textColorPrimary
        ))
    }

    private fun updateSubjectColor(subjectView: TextView, lesson: Timetable) {
        subjectView.setTextColor(subjectView.context.getThemeAttrColor(
            if (lesson.subjectOld.isNotBlank() && lesson.subjectOld != lesson.subject) R.attr.colorTimetableChange
            else android.R.attr.textColorPrimary
        ))
    }

    private fun updateRoomColor(roomView: TextView, lesson: Timetable) {
        roomView.setTextColor(roomView.context.getThemeAttrColor(
            if (lesson.roomOld.isNotBlank() && lesson.roomOld != lesson.room) R.attr.colorTimetableChange
            else android.R.attr.textColorSecondary
        ))
    }

    private fun updateTeacherColor(teacherTextView: TextView, lesson: Timetable) {
        teacherTextView.setTextColor(teacherTextView.context.getThemeAttrColor(
            if (lesson.teacherOld.isNotBlank() && lesson.teacherOld != lesson.teacher) R.attr.colorTimetableChange
            else android.R.attr.textColorSecondary
        ))
    }

    private class ItemViewHolder(val binding: ItemTimetableBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class SmallItemViewHolder(val binding: ItemTimetableSmallBinding) :
        RecyclerView.ViewHolder(binding.root)
}
