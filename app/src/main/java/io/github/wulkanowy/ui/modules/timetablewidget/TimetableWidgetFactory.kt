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
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.data.repositories.timetable.TimetableRepository
import io.github.wulkanowy.ui.modules.timetablewidget.TimetableWidgetProvider.Companion.getDateWidgetKey
import io.github.wulkanowy.ui.modules.timetablewidget.TimetableWidgetProvider.Companion.getStudentWidgetKey
import io.github.wulkanowy.ui.modules.timetablewidget.TimetableWidgetProvider.Companion.getThemeWidgetKey
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.toFormattedString
import io.reactivex.Maybe
import org.threeten.bp.LocalDate
import timber.log.Timber

class TimetableWidgetFactory(
    private val timetableRepository: TimetableRepository,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val sharedPref: SharedPrefProvider,
    private val schedulers: SchedulersProvider,
    private val context: Context,
    private val intent: Intent?
) : RemoteViewsService.RemoteViewsFactory {

    private var lessons = emptyList<Timetable>()

    private var layoutId: Int? = null

    override fun getLoadingView() = null

    override fun hasStableIds() = true

    override fun getCount() = lessons.size

    override fun getViewTypeCount() = 1

    override fun getItemId(position: Int) = position.toLong()

    override fun onCreate() {}

    override fun onDestroy() {}

    override fun onDataSetChanged() {
        intent?.extras?.getInt(EXTRA_APPWIDGET_ID)?.let { appWidgetId ->
            val date = LocalDate.ofEpochDay(sharedPref.getLong(getDateWidgetKey(appWidgetId), 0))
            val studentId = sharedPref.getLong(getStudentWidgetKey(appWidgetId), 0)

            val savedTheme = sharedPref.getLong(getThemeWidgetKey(appWidgetId), 0)
            layoutId = if (savedTheme == 0L) R.layout.item_widget_timetable else R.layout.item_widget_timetable_dark

            lessons = try {
                studentRepository.isStudentSaved()
                    .filter { true }
                    .flatMap { studentRepository.getSavedStudents().toMaybe() }
                    .flatMap {
                        val student = it.singleOrNull { student -> student.id == studentId }

                        if (student != null) Maybe.just(student)
                        else Maybe.empty()
                    }
                    .flatMap { semesterRepository.getCurrentSemester(it).toMaybe() }
                    .flatMap { timetableRepository.getTimetable(it, date, date).toMaybe() }
                    .map { item -> item.sortedBy { it.number } }
                    .subscribeOn(schedulers.backgroundThread)
                    .blockingGet(emptyList())
            } catch (e: Exception) {
                Timber.e(e, "An error has occurred in timetable widget factory")
                emptyList()
            }
        }
    }

    @SuppressLint("DefaultLocale")
    override fun getViewAt(position: Int): RemoteViews? {
        if (position == INVALID_POSITION || lessons.getOrNull(position) == null) return null

        return RemoteViews(context.packageName, layoutId!!).apply {
            val lesson = lessons[position]

            setTextViewText(R.id.timetableWidgetItemSubject, lesson.subject)
            setTextViewText(R.id.timetableWidgetItemNumber, lesson.number.toString())
            setTextViewText(R.id.timetableWidgetItemTime, lesson.start.toFormattedString("HH:mm") +
                " - ${lesson.end.toFormattedString("HH:mm")}")

            if (lesson.room.isNotBlank()) {
                setTextViewText(R.id.timetableWidgetItemRoom, "${context.getString(R.string.timetable_room)} ${lesson.room}")
            } else setTextViewText(R.id.timetableWidgetItemRoom, "")

            if (lesson.info.isNotBlank()) {
                setViewVisibility(R.id.timetableWidgetItemDescription, VISIBLE)
                setTextViewText(R.id.timetableWidgetItemDescription,
                    with(lesson) {
                        when (true) {
                            canceled && !changes -> "Lekcja odwołana: ${lesson.info}"
                            changes && teacher.isNotBlank() -> "Zastępstwo: ${lesson.teacher}"
                            changes && teacher.isBlank() -> "Zastępstwo, ${lesson.info.decapitalize()}"
                            else -> info.capitalize()
                        }
                    })
            } else setViewVisibility(R.id.timetableWidgetItemDescription, GONE)

            if (lesson.canceled) {
                setInt(R.id.timetableWidgetItemSubject, "setPaintFlags",
                    STRIKE_THRU_TEXT_FLAG or ANTI_ALIAS_FLAG)
            } else {
                setInt(R.id.timetableWidgetItemSubject, "setPaintFlags", ANTI_ALIAS_FLAG)
            }

            setOnClickFillInIntent(R.id.timetableWidgetItemContainer, Intent())
        }
    }
}
