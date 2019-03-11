package io.github.wulkanowy.ui.widgets.timetable

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_DELETED
import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import dagger.android.AndroidInjection
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.SharedPrefHelper
import io.github.wulkanowy.services.widgets.TimetableWidgetService
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainActivity.Companion.EXTRA_START_MENU_INDEX
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.nextOrSameSchoolDay
import io.github.wulkanowy.utils.nextSchoolDay
import io.github.wulkanowy.utils.previousSchoolDay
import io.github.wulkanowy.utils.toFormattedString
import io.github.wulkanowy.utils.weekDayName
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDate.now
import javax.inject.Inject

class TimetableWidgetProvider : BroadcastReceiver() {

    @Inject
    lateinit var appWidgetManager: AppWidgetManager

    @Inject
    lateinit var sharedPref: SharedPrefHelper

    @Inject
    lateinit var analytics: FirebaseAnalyticsHelper

    companion object {
        const val EXTRA_TOGGLED_WIDGET_ID = "extraToggledWidget"

        const val EXTRA_BUTTON_TYPE = "extraButtonType"

        const val BUTTON_NEXT = "buttonNext"

        const val BUTTON_PREV = "buttonPrev"

        const val BUTTON_RESET = "buttonReset"

        fun createWidgetKey(appWidgetId: Int) = "timetable_widget_$appWidgetId"
    }

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)
        when (intent.action) {
            ACTION_APPWIDGET_UPDATE -> onUpdate(context, intent)
            ACTION_APPWIDGET_DELETED -> onDelete(intent)
        }
    }

    private fun onUpdate(context: Context, intent: Intent) {
        if (intent.getStringExtra(EXTRA_BUTTON_TYPE) === null) {
            intent.getIntArrayExtra(EXTRA_APPWIDGET_IDS).forEach { appWidgetId ->
                updateWidget(context, appWidgetId, now().nextOrSameSchoolDay)
            }
        } else {
            val buttonType = intent.getStringExtra(EXTRA_BUTTON_TYPE)
            val toggledWidgetId = intent.getIntExtra(EXTRA_TOGGLED_WIDGET_ID, 0)
            val savedDate = LocalDate.ofEpochDay(sharedPref.getLong(createWidgetKey(toggledWidgetId), 0))
            val date = when (buttonType) {
                BUTTON_RESET -> now().nextOrSameSchoolDay
                BUTTON_NEXT -> savedDate.nextSchoolDay
                BUTTON_PREV -> savedDate.previousSchoolDay
                else -> now().nextOrSameSchoolDay
            }
            if (!buttonType.isNullOrBlank()) analytics.logEvent("changed_timetable_widget_day", "button" to buttonType)
            updateWidget(context, toggledWidgetId, date)
        }
    }

    private fun onDelete(intent: Intent) {
        intent.getIntExtra(EXTRA_APPWIDGET_ID, 0).let {
            if (it != 0) sharedPref.delete(createWidgetKey(it))
        }
    }

    private fun updateWidget(context: Context, appWidgetId: Int, date: LocalDate) {
        RemoteViews(context.packageName, R.layout.widget_timetable).apply {
            setEmptyView(R.id.timetableWidgetList, R.id.timetableWidgetEmpty)
            setTextViewText(R.id.timetableWidgetDay, date.weekDayName.capitalize())
            setTextViewText(R.id.timetableWidgetDate, date.toFormattedString())
            setRemoteAdapter(R.id.timetableWidgetList, Intent(context, TimetableWidgetService::class.java)
                .apply { action = createWidgetKey(appWidgetId) })
            setOnClickPendingIntent(R.id.timetableWidgetNext, createNavIntent(context, appWidgetId, appWidgetId, BUTTON_NEXT))
            setOnClickPendingIntent(R.id.timetableWidgetPrev, createNavIntent(context, -appWidgetId, appWidgetId, BUTTON_PREV))
            createNavIntent(context, Int.MAX_VALUE, appWidgetId, BUTTON_RESET).also {
                setOnClickPendingIntent(R.id.timetableWidgetDate, it)
                setOnClickPendingIntent(R.id.timetableWidgetDay, it)
            }
            setPendingIntentTemplate(R.id.timetableWidgetList,
                PendingIntent.getActivity(context, 1, MainActivity.getStartIntent(context).apply {
                    putExtra(EXTRA_START_MENU_INDEX, 3)
                }, FLAG_UPDATE_CURRENT))
        }.also {
            sharedPref.putLong(createWidgetKey(appWidgetId), date.toEpochDay(), true)
            appWidgetManager.apply {
                notifyAppWidgetViewDataChanged(appWidgetId, R.id.timetableWidgetList)
                updateAppWidget(appWidgetId, it)
            }
        }
    }

    private fun createNavIntent(context: Context, code: Int, appWidgetId: Int, buttonType: String): PendingIntent {
        return PendingIntent.getBroadcast(context, code,
            Intent(context, TimetableWidgetProvider::class.java).apply {
                action = ACTION_APPWIDGET_UPDATE
                putExtra(EXTRA_BUTTON_TYPE, buttonType)
                putExtra(EXTRA_TOGGLED_WIDGET_ID, appWidgetId)
            }, FLAG_UPDATE_CURRENT)
    }
}
