package io.github.wulkanowy.ui.modules.timetablewidget

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.content.Context
import android.content.Intent
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView.INVALID_POSITION
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.SharedPrefProvider
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.repositories.TimetableRepository
import io.github.wulkanowy.ui.modules.timetablewidget.TimetableWidgetProvider.Companion.getCurrentThemeWidgetKey
import io.github.wulkanowy.ui.modules.timetablewidget.TimetableWidgetProvider.Companion.getDateWidgetKey
import io.github.wulkanowy.ui.modules.timetablewidget.TimetableWidgetProvider.Companion.getStudentWidgetKey
import io.github.wulkanowy.utils.getCompatColor
import io.github.wulkanowy.utils.toFirstResult
import io.github.wulkanowy.utils.toFormattedString
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.time.LocalDate

class TimetableWidgetFactory(
    private val timetableRepository: TimetableRepository,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val prefRepository: PreferencesRepository,
    private val sharedPref: SharedPrefProvider,
    private val context: Context,
    private val intent: Intent?
) : RemoteViewsService.RemoteViewsFactory {

    private var lessons = emptyList<Timetable>()

    private var savedCurrentTheme: Long? = null

    private var primaryColor: Int? = null

    private var textColor: Int? = null

    private var timetableChangeColor: Int? = null

    override fun getLoadingView() = null

    override fun hasStableIds() = true

    override fun getCount() = lessons.size

    override fun getViewTypeCount() = 2

    override fun getItemId(position: Int) = position.toLong()

    override fun onCreate() {}

    override fun onDestroy() {}

    override fun onDataSetChanged() {
        intent?.extras?.getInt(EXTRA_APPWIDGET_ID)?.let { appWidgetId ->
            val date = LocalDate.ofEpochDay(sharedPref.getLong(getDateWidgetKey(appWidgetId), 0))
            val studentId = sharedPref.getLong(getStudentWidgetKey(appWidgetId), 0)

            updateTheme(appWidgetId)
            lessons = getLessons(date, studentId)
        }
    }

    private fun updateTheme(appWidgetId: Int) {
        savedCurrentTheme = sharedPref.getLong(getCurrentThemeWidgetKey(appWidgetId), 0)

        if (savedCurrentTheme == 0L) {
            primaryColor = R.color.colorPrimary
            textColor = android.R.color.black
            timetableChangeColor = R.color.timetable_change_dark
        } else {
            primaryColor = R.color.colorPrimaryLight
            textColor = android.R.color.white
            timetableChangeColor = R.color.timetable_change_light
        }
    }

    private fun getItemLayout(lesson: Timetable): Int {
        return when {
            prefRepository.showWholeClassPlan == "small" && !lesson.isStudentPlan -> {
                if (savedCurrentTheme == 0L) R.layout.item_widget_timetable_small
                else R.layout.item_widget_timetable_small_dark
            }
            savedCurrentTheme == 1L -> R.layout.item_widget_timetable_dark
            else -> R.layout.item_widget_timetable
        }
    }

    private fun getLessons(date: LocalDate, studentId: Long) = try {
        runBlocking {
            if (!studentRepository.isStudentSaved()) return@runBlocking emptyList<Timetable>()

            val students = studentRepository.getSavedStudents()
            val student = students.singleOrNull { it.student.id == studentId }?.student
                ?: return@runBlocking emptyList<Timetable>()

            val semester = semesterRepository.getCurrentSemester(student)
            timetableRepository.getTimetable(student, semester, date, date, false)
                .toFirstResult().data?.first.orEmpty()
                .sortedWith(compareBy({ it.number }, { !it.isStudentPlan }))
                .filter { if (prefRepository.showWholeClassPlan == "no") it.isStudentPlan else true }
        }
    } catch (e: Exception) {
        Timber.e(e, "An error has occurred in timetable widget factory")
        emptyList()
    }

    @SuppressLint("DefaultLocale")
    override fun getViewAt(position: Int): RemoteViews? {
        if (position == INVALID_POSITION || lessons.getOrNull(position) == null) return null

        val lesson = lessons[position]
        return RemoteViews(context.packageName, getItemLayout(lesson)).apply {
            setTextViewText(R.id.timetableWidgetItemSubject, lesson.subject)
            setTextViewText(R.id.timetableWidgetItemNumber, lesson.number.toString())
            setTextViewText(R.id.timetableWidgetItemTimeStart, lesson.start.toFormattedString("HH:mm"))
            setTextViewText(R.id.timetableWidgetItemTimeFinish, lesson.end.toFormattedString("HH:mm"))

            updateDescription(this, lesson)

            if (lesson.canceled) {
                updateStylesCanceled(this)
            } else {
                updateStylesNotCanceled(this, lesson)
            }

            setOnClickFillInIntent(R.id.timetableWidgetItemContainer, Intent())
        }
    }

    private fun updateDescription(remoteViews: RemoteViews, lesson: Timetable) {
        with(remoteViews) {
            if (lesson.info.isNotBlank() && !lesson.changes) {
                setTextViewText(R.id.timetableWidgetItemDescription, lesson.info)
                setViewVisibility(R.id.timetableWidgetItemDescription, VISIBLE)
                setViewVisibility(R.id.timetableWidgetItemRoom, GONE)
                setViewVisibility(R.id.timetableWidgetItemTeacher, GONE)
            } else {
                setViewVisibility(R.id.timetableWidgetItemDescription, GONE)
                setViewVisibility(R.id.timetableWidgetItemRoom, VISIBLE)
                setViewVisibility(R.id.timetableWidgetItemTeacher, VISIBLE)
            }
        }
    }

    private fun updateStylesCanceled(remoteViews: RemoteViews) {
        with(remoteViews) {
            setInt(R.id.timetableWidgetItemSubject, "setPaintFlags",
                STRIKE_THRU_TEXT_FLAG or ANTI_ALIAS_FLAG)
            setTextColor(R.id.timetableWidgetItemNumber, context.getCompatColor(primaryColor!!))
            setTextColor(R.id.timetableWidgetItemSubject, context.getCompatColor(primaryColor!!))
            setTextColor(R.id.timetableWidgetItemDescription, context.getCompatColor(primaryColor!!))
        }
    }

    private fun updateStylesNotCanceled(remoteViews: RemoteViews, lesson: Timetable) {
        with(remoteViews) {
            setInt(R.id.timetableWidgetItemSubject, "setPaintFlags", ANTI_ALIAS_FLAG)
            setTextColor(R.id.timetableWidgetItemSubject, context.getCompatColor(textColor!!))
            setTextColor(R.id.timetableWidgetItemDescription, context.getCompatColor(timetableChangeColor!!))

            updateNotCanceledLessonNumberColor(this, lesson)
            updateNotCanceledSubjectColor(this, lesson)

            val teacherChange = lesson.teacherOld.isNotBlank() && lesson.teacher != lesson.teacherOld
            updateNotCanceledRoom(this, lesson, teacherChange)
            updateNotCanceledTeacher(this, lesson, teacherChange)
        }
    }

    private fun updateNotCanceledLessonNumberColor(remoteViews: RemoteViews, lesson: Timetable) {
        remoteViews.setTextColor(R.id.timetableWidgetItemNumber, context.getCompatColor(
            if (lesson.changes || (lesson.info.isNotBlank() && !lesson.canceled)) timetableChangeColor!!
            else textColor!!
        ))
    }

    private fun updateNotCanceledSubjectColor(remoteViews: RemoteViews, lesson: Timetable) {
        remoteViews.setTextColor(R.id.timetableWidgetItemSubject, context.getCompatColor(
            if (lesson.subjectOld.isNotBlank() && lesson.subject != lesson.subjectOld) timetableChangeColor!!
            else textColor!!
        ))
    }

    private fun updateNotCanceledRoom(remoteViews: RemoteViews, lesson: Timetable, teacherChange: Boolean) {
        with(remoteViews) {
            if (lesson.room.isNotBlank()) {
                setTextViewText(R.id.timetableWidgetItemRoom,
                    if (teacherChange) lesson.room
                    else "${context.getString(R.string.timetable_room)} ${lesson.room}"
                )

                setTextColor(R.id.timetableWidgetItemRoom, context.getCompatColor(
                    if (lesson.roomOld.isNotBlank() && lesson.room != lesson.roomOld) timetableChangeColor!!
                    else textColor!!
                ))
            } else setTextViewText(R.id.timetableWidgetItemRoom, "")
        }
    }

    private fun updateNotCanceledTeacher(remoteViews: RemoteViews, lesson: Timetable, teacherChange: Boolean) {
        remoteViews.setTextViewText(R.id.timetableWidgetItemTeacher,
            if (teacherChange) lesson.teacher
            else ""
        )
    }
}
