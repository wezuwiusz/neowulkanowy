package io.github.wulkanowy.ui.modules.timetablewidget

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
import io.github.wulkanowy.data.db.SharedPrefHelper
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.data.repositories.timetable.TimetableRepository
import io.github.wulkanowy.ui.modules.timetablewidget.TimetableWidgetProvider.Companion.getDateWidgetKey
import io.github.wulkanowy.ui.modules.timetablewidget.TimetableWidgetProvider.Companion.getStudentWidgetKey
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.toFormattedString
import io.reactivex.Maybe
import org.threeten.bp.LocalDate
import timber.log.Timber

class TimetableWidgetFactory(
    private val timetableRepository: TimetableRepository,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val sharedPref: SharedPrefHelper,
    private val schedulers: SchedulersProvider,
    private val context: Context,
    private val intent: Intent?
) : RemoteViewsService.RemoteViewsFactory {

    private var lessons = emptyList<Timetable>()

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

            lessons = try {
                studentRepository.isStudentSaved()
                    .filter { true }
                    .flatMap { studentRepository.getSavedStudents().toMaybe() }
                    .flatMap {
                        if (studentId == 0L) throw IllegalArgumentException("Student id is 0")

                        it.singleOrNull { student -> student.id == studentId }
                            .let { student ->
                                if (student != null) Maybe.just(student)
                                else Maybe.empty()
                            }
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

    override fun getViewAt(position: Int): RemoteViews? {
        if (position == INVALID_POSITION || lessons.getOrNull(position) == null) return null

        return RemoteViews(context.packageName, R.layout.item_widget_timetable).apply {
            lessons[position].let {
                setTextViewText(R.id.timetableWidgetItemSubject, it.subject)
                setTextViewText(R.id.timetableWidgetItemNumber, it.number.toString())
                setTextViewText(R.id.timetableWidgetItemTime, it.start.toFormattedString("HH:mm") +
                    " - ${it.end.toFormattedString("HH:mm")}")

                if (it.room.isNotBlank()) {
                    setTextViewText(R.id.timetableWidgetItemRoom, "${context.getString(R.string.timetable_room)} ${it.room}")
                } else setTextViewText(R.id.timetableWidgetItemRoom, "")

                if (it.info.isNotBlank()) {
                    setViewVisibility(R.id.timetableWidgetItemDescription, VISIBLE)
                    setTextViewText(R.id.timetableWidgetItemDescription, it.run {
                        when (true) {
                            canceled && !changes -> "Lekcja odwołana: $info"
                            changes && teacher.isNotBlank() -> "Zastępstwo: $teacher"
                            changes && teacher.isBlank() -> "Zastępstwo, ${info.decapitalize()}"
                            else -> it.info.capitalize()
                        }
                    })
                } else setViewVisibility(R.id.timetableWidgetItemDescription, GONE)

                if (it.canceled) {
                    setInt(R.id.timetableWidgetItemSubject, "setPaintFlags",
                        STRIKE_THRU_TEXT_FLAG or ANTI_ALIAS_FLAG)
                } else {
                    setInt(R.id.timetableWidgetItemSubject, "setPaintFlags", ANTI_ALIAS_FLAG)
                }
            }
            setOnClickFillInIntent(R.id.timetableWidgetItemContainer, Intent())
        }
    }
}
