package io.github.wulkanowy.ui.modules.timetable

import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
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
import timber.log.Timber
import java.time.LocalDateTime
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.timer

class TimetableAdapter @Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private enum class ViewType {
        ITEM_NORMAL,
        ITEM_SMALL
    }

    var onClickListener: (Timetable) -> Unit = {}

    private var showWholeClassPlan: String = "no"

    private var showGroupsInPlan: Boolean = false

    private var showTimers: Boolean = false

    private val timers = mutableMapOf<Int, Timer?>()

    private val items = mutableListOf<Timetable>()

    fun submitList(
        newTimetable: List<Timetable>,
        showWholeClassPlan: String = this.showWholeClassPlan,
        showGroupsInPlan: Boolean = this.showGroupsInPlan,
        showTimers: Boolean = this.showTimers
    ) {
        val isFlagsDifferent = this.showWholeClassPlan != showWholeClassPlan
            || this.showGroupsInPlan != showGroupsInPlan
            || this.showTimers != showTimers

        val diffResult = DiffUtil.calculateDiff(
            TimetableAdapterDiffCallback(
                oldList = items.toMutableList(),
                newList = newTimetable,
                isFlagsDifferent = isFlagsDifferent
            )
        )

        this.showGroupsInPlan = showGroupsInPlan
        this.showTimers = showTimers
        this.showWholeClassPlan = showWholeClassPlan

        items.clear()
        items.addAll(newTimetable)

        diffResult.dispatchUpdatesTo(this)
    }

    fun clearTimers() {
        Timber.d("Timetable timers (${timers.size}) cleared")
        with(timers) {
            forEach { (_, timer) ->
                timer?.cancel()
                timer?.purge()
            }
            clear()
        }
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = when {
        !items[position].isStudentPlan && showWholeClassPlan == "small" -> ViewType.ITEM_SMALL.ordinal
        else -> ViewType.ITEM_NORMAL.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            ViewType.ITEM_NORMAL.ordinal -> ItemViewHolder(
                ItemTimetableBinding.inflate(inflater, parent, false)
            )
            ViewType.ITEM_SMALL.ordinal -> SmallItemViewHolder(
                ItemTimetableSmallBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException()
        }
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
            timetableItemGroup.text = lesson.group
            timetableItemRoom.text = lesson.room
            timetableItemTeacher.text = lesson.teacher
            timetableItemTimeStart.text = lesson.start.toFormattedString("HH:mm")
            timetableItemTimeFinish.text = lesson.end.toFormattedString("HH:mm")

            bindSubjectStyle(timetableItemSubject, lesson)
            bindNormalDescription(binding, lesson)
            bindNormalColors(binding, lesson)

            timers[position]?.let {
                it.cancel()
                it.purge()
            }
            timers[position] = null

            if (lesson.isStudentPlan && showTimers) {
                timers[position] = timer(period = 1000) {
                    Handler(Looper.getMainLooper()).post {
                        updateTimeLeft(binding, lesson, position)
                    }
                }
            } else {
                // reset item on set changed
                timetableItemTimeUntil.visibility = GONE
                timetableItemTimeLeft.visibility = GONE
            }

            root.setOnClickListener { onClickListener(lesson) }
        }
    }

    private fun getPreviousLesson(position: Int): LocalDateTime? {
        return items.filter { it.isStudentPlan }
            .getOrNull(position - 1 - items.filterIndexed { i, item -> i < position && !item.isStudentPlan }.size)
            ?.let {
                if (!it.canceled && it.isStudentPlan) it.end
                else null
            }
    }

    private fun updateTimeLeft(binding: ItemTimetableBinding, lesson: Timetable, position: Int) {
        val isShowTimeUntil = lesson.isShowTimeUntil(getPreviousLesson(position))
        val until = lesson.until.plusMinutes(1)
        val left = lesson.left?.plusMinutes(1)
        val isJustFinished = lesson.isJustFinished

        with(binding) {
            when {
                // before lesson
                isShowTimeUntil -> {
                    Timber.d("Show time until lesson: $position")
                    timetableItemTimeLeft.visibility = GONE
                    with(timetableItemTimeUntil) {
                        visibility = VISIBLE
                        text = context.getString(
                            R.string.timetable_time_until,
                            context.getString(
                                R.string.timetable_minutes,
                                until.toMinutes().toString(10)
                            )
                        )
                    }
                }
                // after lesson start
                left != null -> {
                    Timber.d("Show time left lesson: $position")
                    timetableItemTimeUntil.visibility = GONE
                    with(timetableItemTimeLeft) {
                        visibility = VISIBLE
                        text = context.getString(
                            R.string.timetable_time_left,
                            context.getString(
                                R.string.timetable_minutes,
                                left.toMinutes().toString()
                            )
                        )
                    }
                }
                // right after lesson finish
                isJustFinished -> {
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
        subjectView.paintFlags =
            if (lesson.canceled) subjectView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            else subjectView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }

    private fun bindSmallDescription(binding: ItemTimetableSmallBinding, lesson: Timetable) {
        with(binding) {
            if (lesson.info.isNotBlank() && !lesson.changes) {
                timetableSmallItemDescription.visibility = VISIBLE
                timetableSmallItemDescription.text = lesson.info

                timetableSmallItemRoom.visibility = GONE
                timetableSmallItemTeacher.visibility = GONE

                timetableSmallItemDescription.setTextColor(
                    root.context.getThemeAttrColor(
                        if (lesson.canceled) R.attr.colorPrimary
                        else R.attr.colorTimetableChange
                    )
                )
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
                timetableItemGroup.visibility = GONE
                timetableItemTeacher.visibility = GONE

                timetableItemDescription.setTextColor(
                    root.context.getThemeAttrColor(
                        if (lesson.canceled) R.attr.colorPrimary
                        else R.attr.colorTimetableChange
                    )
                )
            } else {
                timetableItemDescription.visibility = GONE
                timetableItemRoom.visibility = VISIBLE
                timetableItemGroup.visibility =
                    if (showGroupsInPlan && lesson.group.isNotBlank()) VISIBLE else GONE
                timetableItemTeacher.visibility = VISIBLE
            }
        }
    }

    private fun bindSmallColors(binding: ItemTimetableSmallBinding, lesson: Timetable) {
        with(binding) {
            if (lesson.canceled) {
                updateNumberAndSubjectCanceledColor(
                    timetableSmallItemNumber,
                    timetableSmallItemSubject
                )
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
        numberView.setTextColor(
            numberView.context.getThemeAttrColor(
                if (lesson.changes || lesson.info.isNotBlank()) R.attr.colorTimetableChange
                else android.R.attr.textColorPrimary
            )
        )
    }

    private fun updateSubjectColor(subjectView: TextView, lesson: Timetable) {
        subjectView.setTextColor(
            subjectView.context.getThemeAttrColor(
                if (lesson.subjectOld.isNotBlank() && lesson.subjectOld != lesson.subject) R.attr.colorTimetableChange
                else android.R.attr.textColorPrimary
            )
        )
    }

    private fun updateRoomColor(roomView: TextView, lesson: Timetable) {
        roomView.setTextColor(
            roomView.context.getThemeAttrColor(
                if (lesson.roomOld.isNotBlank() && lesson.roomOld != lesson.room) R.attr.colorTimetableChange
                else android.R.attr.textColorSecondary
            )
        )
    }

    private fun updateTeacherColor(teacherTextView: TextView, lesson: Timetable) {
        teacherTextView.setTextColor(
            teacherTextView.context.getThemeAttrColor(
                if (lesson.teacherOld.isNotBlank()) R.attr.colorTimetableChange
                else android.R.attr.textColorSecondary
            )
        )
    }

    private class ItemViewHolder(val binding: ItemTimetableBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class SmallItemViewHolder(val binding: ItemTimetableSmallBinding) :
        RecyclerView.ViewHolder(binding.root)

    class TimetableAdapterDiffCallback(
        private val oldList: List<Timetable>,
        private val newList: List<Timetable>,
        private val isFlagsDifferent: Boolean
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition].id == newList[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition] == newList[newItemPosition] && !isFlagsDifferent
    }
}
