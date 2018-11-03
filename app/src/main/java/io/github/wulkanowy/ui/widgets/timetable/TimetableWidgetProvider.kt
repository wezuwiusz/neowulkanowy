package io.github.wulkanowy.ui.widgets.timetable

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import dagger.android.AndroidInjection
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.SharedPrefHelper
import io.github.wulkanowy.services.widgets.TimetableWidgetService
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainActivity.Companion.EXTRA_START_MENU_INDEX
import io.github.wulkanowy.utils.logEvent
import io.github.wulkanowy.utils.nextOrSameSchoolDay
import io.github.wulkanowy.utils.nextSchoolDay
import io.github.wulkanowy.utils.previousSchoolDay
import io.github.wulkanowy.utils.toFormattedString
import io.github.wulkanowy.utils.weekDayName
import org.threeten.bp.LocalDate
import javax.inject.Inject

class TimetableWidgetProvider : AppWidgetProvider() {

    @Inject
    lateinit var sharedPref: SharedPrefHelper

    companion object {
        const val EXTRA_TOGGLED_WIDGET_ID = "extraToggledWidget"

        const val EXTRA_BUTTON_TYPE = "extraButtonType"

        const val BUTTON_NEXT = "buttonNext"

        const val BUTTON_PREV = "buttonPrev"

        const val BUTTON_RESET = "buttonReset"
    }

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        appWidgetIds?.forEach {
            val widgetKey = "timetable_widget_$it"
            checkSavedWidgetDate(widgetKey)

            val savedDate = LocalDate.ofEpochDay(sharedPref.getLong(widgetKey, 0))
            context?.run {
                RemoteViews(packageName, R.layout.widget_timetable).apply {
                    setTextViewText(R.id.timetableWidgetDay, savedDate.weekDayName.capitalize())
                    setTextViewText(R.id.timetableWidgetDate, savedDate.toFormattedString())
                    setEmptyView(R.id.timetableWidgetList, R.id.timetableWidgetEmpty)
                    setRemoteAdapter(R.id.timetableWidgetList, Intent(context, TimetableWidgetService::class.java)
                        .apply { action = widgetKey })
                    setOnClickPendingIntent(R.id.timetableWidgetNext, createNavIntent(context, it, it, appWidgetIds, BUTTON_NEXT))
                    setOnClickPendingIntent(R.id.timetableWidgetPrev, createNavIntent(context, -it, it, appWidgetIds, BUTTON_PREV))
                    createNavIntent(context, Int.MAX_VALUE, it, appWidgetIds, BUTTON_RESET).let { intent ->
                        setOnClickPendingIntent(R.id.timetableWidgetDate, intent)
                        setOnClickPendingIntent(R.id.timetableWidgetDay, intent)
                    }
                    setPendingIntentTemplate(R.id.timetableWidgetList,
                        PendingIntent.getActivity(context, 1, MainActivity.getStartIntent(context).apply {
                            putExtra(EXTRA_START_MENU_INDEX, 3)
                        }, FLAG_UPDATE_CURRENT))

                }.also { view ->
                    appWidgetManager?.apply {
                        notifyAppWidgetViewDataChanged(it, R.id.timetableWidgetList)
                        updateAppWidget(it, view)
                    }
                }
            }
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        AndroidInjection.inject(this, context)
        intent?.let {
            val widgetKey = "timetable_widget_${it.getIntExtra(EXTRA_TOGGLED_WIDGET_ID, 0)}"
            it.getStringExtra(EXTRA_BUTTON_TYPE).let { button ->
                when (button) {
                    BUTTON_NEXT -> {
                        LocalDate.ofEpochDay(sharedPref.getLong(widgetKey, 0)).nextSchoolDay
                            .let { date -> sharedPref.putLong(widgetKey, date.toEpochDay(), true) }
                    }
                    BUTTON_PREV -> {
                        LocalDate.ofEpochDay(sharedPref.getLong(widgetKey, 0)).previousSchoolDay
                            .let { date -> sharedPref.putLong(widgetKey, date.toEpochDay(), true) }
                    }
                    BUTTON_RESET -> sharedPref.putLong(widgetKey, LocalDate.now().nextOrSameSchoolDay.toEpochDay(), true)
                }
                button?.also { btn -> if (btn.isNotBlank()) logEvent("Widget day changed", mapOf("button" to button)) }
            }
        }
        super.onReceive(context, intent)
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        appWidgetIds?.forEach {
            sharedPref.delete("timetable_widget_$it")
        }
    }

    private fun createNavIntent(context: Context, code: Int, widgetId: Int, widgetIds: IntArray, buttonType: String): PendingIntent {
        return PendingIntent.getBroadcast(context, code,
            Intent(context, TimetableWidgetProvider::class.java).apply {
                action = ACTION_APPWIDGET_UPDATE
                putExtra(EXTRA_APPWIDGET_IDS, widgetIds)
                putExtra(EXTRA_BUTTON_TYPE, buttonType)
                putExtra(EXTRA_TOGGLED_WIDGET_ID, widgetId)
            }, FLAG_UPDATE_CURRENT)
    }

    private fun checkSavedWidgetDate(widgetKey: String) {
        sharedPref.run {
            if (getLong(widgetKey, -1) == -1L) {
                putLong(widgetKey, LocalDate.now().nextOrSameSchoolDay.toEpochDay(), true)
            }
        }
    }
}
