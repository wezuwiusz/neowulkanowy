package io.github.wulkanowy.ui.modules.timetable

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.databinding.ItemTimetableBinding
import io.github.wulkanowy.databinding.ItemTimetableSmallBinding
import io.github.wulkanowy.utils.getThemeAttrColor
import io.github.wulkanowy.utils.isJustFinished
import io.github.wulkanowy.utils.isShowTimeUntil
import io.github.wulkanowy.utils.left
import io.github.wulkanowy.utils.toFormattedString
import io.github.wulkanowy.utils.until
import org.threeten.bp.LocalDateTime
import timber.log.Timber
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.timer

class TimetableAdapter @Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private enum class ViewType(val id: Int) {
        ITEM_NORMAL(1),
        ITEM_SMALL(2)
    }

    var items = mutableListOf<Timetable>()
        set(value) {
            field = value
            resetTimers()
        }

    var onClickListener: (Timetable) -> Unit = {}

    var showWholeClassPlan: String = "no"

    var showTimers: Boolean = false

    private val timers = mutableMapOf<Int, Timer>()

    private fun resetTimers() {
        Timber.d("Timetable timers reset")
        with(timers) {
            forEach { (_, timer) -> timer.cancel() }
            clear()
        }
    }

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

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        resetTimers()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val lesson = items[position]

        when (holder) {
            is ItemViewHolder -> bindNormalView(holder.binding, lesson, position)
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

    private fun bindNormalView(binding: ItemTimetableBinding, lesson: Timetable, position: Int) {
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

            if (lesson.isStudentPlan && showTimers) timers[position] = timer(period = 1000) {
                root.post { updateTimeLeft(binding, lesson, position) }
            } else {
                // reset item on set changed
                timetableItemTimeUntil.visibility = GONE
                timetableItemTimeLeft.visibility = GONE
            }

            root.setOnClickListener { onClickListener(lesson) }
        }
    }

    private fun getPreviousLesson(position: Int): LocalDateTime? {
        return items.filter { it.isStudentPlan }.getOrNull(position - 1 - items.filterIndexed { i, item -> i < position && !item.isStudentPlan }.size)?.let {
            if (!it.canceled && it.isStudentPlan) it.end
            else null
        }
    }

    private fun updateTimeLeft(binding: ItemTimetableBinding, lesson: Timetable, position: Int) {
        with(binding) {
            when {
                // before lesson
                lesson.isShowTimeUntil(getPreviousLesson(position)) -> {
                    Timber.d("Show time until lesson: $position")
                    timetableItemTimeLeft.visibility = GONE
                    with(timetableItemTimeUntil) {
                        visibility = VISIBLE
                        text = context.getString(R.string.timetable_time_until,
                            if (lesson.until.seconds <= 60) {
                                context.getString(R.string.timetable_seconds, lesson.until.seconds.toString(10))
                            } else {
                                context.getString(R.string.timetable_minutes, lesson.until.toMinutes().toString(10))
                            }
                        )
                    }
                }
                // after lesson start
                lesson.left != null -> {
                    Timber.d("Show time left lesson: $position")
                    timetableItemTimeUntil.visibility = GONE
                    with(timetableItemTimeLeft) {
                        visibility = VISIBLE
                        text = context.getString(
                            R.string.timetable_time_left,
                            if (lesson.left!!.seconds < 60) {
                                context.getString(R.string.timetable_seconds, lesson.left?.seconds?.toString(10))
                            } else {
                                context.getString(R.string.timetable_minutes, lesson.left?.toMinutes()?.toString(10))
                            }
                        )
                    }
                }
                // right after lesson finish
                lesson.isJustFinished -> {
                    Timber.d("Show just finished lesson: $position")
                    timetableItemTimeUntil.visibility = GONE
                    timetableItemTimeLeft.visibility = VISIBLE
                    timetableItemTimeLeft.text = root.context.getString(R.string.timetable_finished)
                }
                else -> {
                    timetableItemTimeUntil.visibility = GONE
                    timetableItemTimeLeft.visibility = GONE
                }
            }
        }
    }

    private fun bindSubjectStyle(subjectView: TextView, lesson: Timetable) {
        subjectView.paintFlags = if (lesson.canceled) subjectView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        else subjectView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }

    private fun bindSmallDescription(binding: ItemTimetableSmallBinding, lesson: Timetable) {
        with(binding) {
            if (lesson.info.isNotBlank() && !lesson.changes) {
                timetableSmallItemDescription.visibility = VISIBLE
                timetableSmallItemDescription.text = lesson.info

                timetableSmallItemRoom.visibility = GONE
                timetableSmallItemTeacher.visibility = GONE

                timetableSmallItemDescription.setTextColor(root.context.getThemeAttrColor(
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

    private fun bindNormalDescription(binding: ItemTimetableBinding, lesson: Timetable) {
        with(binding) {
            if (lesson.info.isNotBlank() && !lesson.changes) {
                timetableItemDescription.visibility = VISIBLE
                timetableItemDescription.text = lesson.info

                timetableItemRoom.visibility = GONE
                timetableItemTeacher.visibility = GONE

                timetableItemDescription.setTextColor(root.context.getThemeAttrColor(
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
