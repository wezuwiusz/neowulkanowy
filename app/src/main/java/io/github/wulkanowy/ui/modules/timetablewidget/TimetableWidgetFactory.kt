package io.github.wulkanowy.ui.modules.timetablewidget

import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import io.github.wulkanowy.R
import io.github.wulkanowy.data.dataOrNull
import io.github.wulkanowy.data.db.SharedPrefProvider
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.repositories.TimetableRepository
import io.github.wulkanowy.data.toFirstResult
import io.github.wulkanowy.ui.modules.timetablewidget.TimetableWidgetProvider.Companion.getDateWidgetKey
import io.github.wulkanowy.ui.modules.timetablewidget.TimetableWidgetProvider.Companion.getStudentWidgetKey
import io.github.wulkanowy.ui.modules.timetablewidget.TimetableWidgetProvider.Companion.getTodayLastLessonEndDateTimeWidgetKey
import io.github.wulkanowy.utils.getCompatColor
import io.github.wulkanowy.utils.toFormattedString
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.time.Instant
import java.time.LocalDate

class TimetableWidgetFactory(
    private val timetableRepository: TimetableRepository,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val sharedPref: SharedPrefProvider,
    private val context: Context,
    private val intent: Intent?
) : RemoteViewsService.RemoteViewsFactory {

    private var lessons = emptyList<Timetable>()

    private var timetableCanceledColor: Int? = null

    private var textColor: Int? = null

    private var timetableChangeColor: Int? = null

    private var lastSyncInstant: Instant? = null

    override fun getLoadingView() = null

    override fun hasStableIds() = true

    override fun getCount() = when {
        lessons.isEmpty() -> 0
        else -> lessons.size + 1
    }

    override fun getViewTypeCount() = 2

    override fun getItemId(position: Int) = position.toLong()

    override fun onCreate() {}

    override fun onDestroy() {}

    override fun onDataSetChanged() {
        intent?.extras?.getInt(EXTRA_APPWIDGET_ID)?.let { appWidgetId ->
            val date = LocalDate.ofEpochDay(sharedPref.getLong(getDateWidgetKey(appWidgetId), 0))
            val studentId = sharedPref.getLong(getStudentWidgetKey(appWidgetId), 0)

            runCatching {
                runBlocking {
                    val student = getStudent(studentId) ?: return@runBlocking
                    val semester = semesterRepository.getCurrentSemester(student)
                    lessons = getLessons(student, semester, date)
                    lastSyncInstant =
                        timetableRepository.getLastRefreshTimestamp(semester, date, date)
                    if (date == LocalDate.now()) {
                        updateTodayLastLessonEnd(appWidgetId)
                    }
                }
            }.onFailure {
                Timber.e(it, "An error has occurred in timetable widget factory")
            }
        }
    }

    private suspend fun getStudent(studentId: Long): Student? {
        val students = studentRepository.getSavedStudents()
        return students.singleOrNull { it.student.id == studentId }?.student
    }

    private suspend fun getLessons(
        student: Student, semester: Semester, date: LocalDate
    ): List<Timetable> {
        val timetable = timetableRepository.getTimetable(student, semester, date, date, false)
        val lessons = timetable.toFirstResult().dataOrNull?.lessons.orEmpty()
        return lessons.sortedBy { it.number }
    }

    private fun updateTodayLastLessonEnd(appWidgetId: Int) {
        val todayLastLessonEnd = lessons.maxOfOrNull { it.end } ?: return
        val key = getTodayLastLessonEndDateTimeWidgetKey(appWidgetId)
        sharedPref.putLong(key, todayLastLessonEnd.epochSecond, true)
    }

    companion object {
        const val TIME_FORMAT_STYLE = "HH:mm"
    }

    override fun getViewAt(position: Int): RemoteViews? {
        if (position == lessons.size) {
            val synchronizationInstant = lastSyncInstant ?: Instant.MIN
            val synchronizationText = getSynchronizationInfoText(synchronizationInstant)
            return RemoteViews(context.packageName, R.layout.item_widget_timetable_footer).apply {
                setTextViewText(R.id.timetableWidgetSynchronizationTime, synchronizationText)
            }
        }

        val lesson = lessons.getOrNull(position) ?: return null

        val lessonStartTime = lesson.start.toFormattedString(TIME_FORMAT_STYLE)
        val lessonEndTime = lesson.end.toFormattedString(TIME_FORMAT_STYLE)
        val roomText = "${context.getString(R.string.timetable_room)} ${lesson.room}"

        val remoteViews = RemoteViews(context.packageName, R.layout.item_widget_timetable).apply {
            setTextViewText(R.id.timetableWidgetItemNumber, lesson.number.toString())
            setTextViewText(R.id.timetableWidgetItemTimeStart, lessonStartTime)
            setTextViewText(R.id.timetableWidgetItemTimeFinish, lessonEndTime)
            setTextViewText(R.id.timetableWidgetItemSubject, lesson.subject)
            setTextViewText(R.id.timetableWidgetItemRoom, roomText)
            setTextViewText(R.id.timetableWidgetItemTeacher, lesson.teacher)
            setTextViewText(R.id.timetableWidgetItemDescription, lesson.info)
            setOnClickFillInIntent(R.id.timetableWidgetItemContainer, Intent())
        }

        updateTheme()
        clearLessonStyles(remoteViews)

        when {
            lesson.canceled -> applyCancelledLessonStyles(remoteViews)
            lesson.changes or lesson.info.isNotBlank() -> applyChangedLessonStyles(
                remoteViews, lesson
            )
        }

        return remoteViews
    }

    private fun updateTheme() {
        when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                textColor = android.R.color.white
                timetableChangeColor = R.color.timetable_change_dark
                timetableCanceledColor = R.color.timetable_canceled_dark
            }

            else -> {
                textColor = android.R.color.black
                timetableChangeColor = R.color.timetable_change_light
                timetableCanceledColor = R.color.timetable_canceled_light
            }
        }
    }

    private fun clearLessonStyles(remoteViews: RemoteViews) {
        val defaultTextColor = context.getCompatColor(textColor ?: 0)

        remoteViews.apply {
            setInt(R.id.timetableWidgetItemSubject, "setPaintFlags", ANTI_ALIAS_FLAG)
            setViewVisibility(R.id.timetableWidgetItemRoom, VISIBLE)
            setViewVisibility(R.id.timetableWidgetItemTeacher, VISIBLE)
            setViewVisibility(R.id.timetableWidgetItemIcon, GONE)
            setViewVisibility(R.id.timetableWidgetItemDescription, GONE)
            setTextColor(R.id.timetableWidgetItemNumber, defaultTextColor)
            setTextColor(R.id.timetableWidgetItemSubject, defaultTextColor)
            setTextColor(R.id.timetableWidgetItemRoom, defaultTextColor)
            setTextColor(R.id.timetableWidgetItemTeacher, defaultTextColor)
            setTextColor(R.id.timetableWidgetItemDescription, defaultTextColor)
        }
    }

    private fun applyCancelledLessonStyles(remoteViews: RemoteViews) {
        val cancelledThemeColor = context.getCompatColor(timetableCanceledColor ?: 0)
        val strikeThroughPaintFlags = STRIKE_THRU_TEXT_FLAG or ANTI_ALIAS_FLAG

        remoteViews.apply {
            setInt(R.id.timetableWidgetItemSubject, "setPaintFlags", strikeThroughPaintFlags)
            setTextColor(R.id.timetableWidgetItemNumber, cancelledThemeColor)
            setTextColor(R.id.timetableWidgetItemSubject, cancelledThemeColor)
            setTextColor(R.id.timetableWidgetItemDescription, cancelledThemeColor)
            setViewVisibility(R.id.timetableWidgetItemDescription, VISIBLE)
            setViewVisibility(R.id.timetableWidgetItemRoom, GONE)
            setViewVisibility(R.id.timetableWidgetItemTeacher, GONE)
        }
    }

    private fun applyChangedLessonStyles(remoteViews: RemoteViews, lesson: Timetable) {
        val changesTextColor = context.getCompatColor(timetableChangeColor ?: 0)

        remoteViews.apply {
            setTextColor(R.id.timetableWidgetItemNumber, changesTextColor)
            setTextColor(R.id.timetableWidgetItemDescription, changesTextColor)
            setViewVisibility(R.id.timetableWidgetItemIcon, VISIBLE)
            setImageViewResource(R.id.timetableWidgetItemIcon, R.drawable.ic_timetable_widget_swap)
        }

        if (lesson.subject != lesson.subjectOld) {
            remoteViews.setTextColor(R.id.timetableWidgetItemSubject, changesTextColor)
        }

        if (lesson.room != lesson.roomOld) {
            remoteViews.setTextColor(R.id.timetableWidgetItemRoom, changesTextColor)
        }

        if (lesson.teacher != lesson.teacherOld) {
            remoteViews.setTextColor(R.id.timetableWidgetItemTeacher, changesTextColor)
        }

        if (lesson.info.isNotBlank() && !lesson.changes) {
            remoteViews.setViewVisibility(R.id.timetableWidgetItemDescription, VISIBLE)
            remoteViews.setViewVisibility(R.id.timetableWidgetItemRoom, GONE)
            remoteViews.setViewVisibility(R.id.timetableWidgetItemTeacher, GONE)
        }
    }

    private fun getSynchronizationInfoText(synchronizationInstant: Instant) =
        synchronizationInstant.run {
            val synchronizationTime = toFormattedString(TIME_FORMAT_STYLE)
            val synchronizationDate = toFormattedString()
            context.getString(
                R.string.widget_timetable_last_synchronization,
                synchronizationDate,
                synchronizationTime,
            )
        }
}
