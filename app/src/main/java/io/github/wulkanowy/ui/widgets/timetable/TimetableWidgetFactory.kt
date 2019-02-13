package io.github.wulkanowy.ui.widgets.timetable

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
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.repositories.TimetableRepository
import io.github.wulkanowy.utils.toFormattedString
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import org.threeten.bp.LocalDate
import timber.log.Timber

class TimetableWidgetFactory(
    private val timetableRepository: TimetableRepository,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val sharedPref: SharedPrefHelper,
    private val context: Context,
    private val intent: Intent?
) : RemoteViewsService.RemoteViewsFactory {

    private val disposable: CompositeDisposable = CompositeDisposable()

    private var lessons = emptyList<Timetable>()

    override fun getLoadingView() = null

    override fun hasStableIds() = true

    override fun getCount() = lessons.size

    override fun getViewTypeCount() = 1

    override fun getItemId(position: Int) = position.toLong()

    override fun onCreate() {}

    override fun onDataSetChanged() {
        intent?.action?.let { LocalDate.ofEpochDay(sharedPref.getLong(it, 0)) }
            ?.let { date ->
                disposable.add(studentRepository.isStudentSaved()
                    .flatMap {
                        if (it) studentRepository.getCurrentStudent()
                        else Single.error(IllegalArgumentException("No saved students"))
                    }.flatMap { semesterRepository.getCurrentSemester(it) }
                    .flatMap { timetableRepository.getTimetable(it, date, date) }
                    .map { item -> item.sortedBy { it.number } }
                    .subscribe({ lessons = it })
                    { Timber.e(it, "An error has occurred while downloading data for the widget") })
            }
    }

    override fun getViewAt(position: Int): RemoteViews? {
        if (position == INVALID_POSITION) return null

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
                    setTextViewText(R.id.timetableWidgetItemDescription, it.info.capitalize())
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

    override fun onDestroy() {
        disposable.clear()
    }
}
