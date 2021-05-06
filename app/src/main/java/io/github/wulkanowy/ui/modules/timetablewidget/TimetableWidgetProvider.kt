package io.github.wulkanowy.ui.modules.timetablewidget

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_DELETED
import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.widget.RemoteViews
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.SharedPrefProvider
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.exceptions.NoCurrentStudentException
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.services.HiltBroadcastReceiver
import io.github.wulkanowy.services.widgets.TimetableWidgetService
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.capitalise
import io.github.wulkanowy.utils.createNameInitialsDrawable
import io.github.wulkanowy.utils.getCompatColor
import io.github.wulkanowy.utils.nextOrSameSchoolDay
import io.github.wulkanowy.utils.nextSchoolDay
import io.github.wulkanowy.utils.nickOrName
import io.github.wulkanowy.utils.previousSchoolDay
import io.github.wulkanowy.utils.toFormattedString
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDate.now
import javax.inject.Inject

@AndroidEntryPoint
class TimetableWidgetProvider : HiltBroadcastReceiver() {

    @Inject
    lateinit var appWidgetManager: AppWidgetManager

    @Inject
    lateinit var studentRepository: StudentRepository

    @Inject
    lateinit var sharedPref: SharedPrefProvider

    @Inject
    lateinit var analytics: AnalyticsHelper

    companion object {

        private const val EXTRA_TOGGLED_WIDGET_ID = "extraToggledWidget"

        private const val EXTRA_BUTTON_TYPE = "extraButtonType"

        private const val BUTTON_NEXT = "buttonNext"

        private const val BUTTON_PREV = "buttonPrev"

        private const val BUTTON_RESET = "buttonReset"

        const val EXTRA_FROM_PROVIDER = "extraFromProvider"

        fun getDateWidgetKey(appWidgetId: Int) = "timetable_widget_date_$appWidgetId"

        fun getStudentWidgetKey(appWidgetId: Int) = "timetable_widget_student_$appWidgetId"

        fun getThemeWidgetKey(appWidgetId: Int) = "timetable_widget_theme_$appWidgetId"

        fun getCurrentThemeWidgetKey(appWidgetId: Int) =
            "timetable_widget_current_theme_$appWidgetId"
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        GlobalScope.launch {
            when (intent.action) {
                ACTION_APPWIDGET_UPDATE -> onUpdate(context, intent)
                ACTION_APPWIDGET_DELETED -> onDelete(intent)
            }
        }
    }

    private suspend fun onUpdate(context: Context, intent: Intent) {
        if (intent.getStringExtra(EXTRA_BUTTON_TYPE) === null) {
            intent.getIntArrayExtra(EXTRA_APPWIDGET_IDS)?.forEach { appWidgetId ->
                val student =
                    getStudent(sharedPref.getLong(getStudentWidgetKey(appWidgetId), 0), appWidgetId)
                updateWidget(context, appWidgetId, now().nextOrSameSchoolDay, student)
            }
        } else {
            val buttonType = intent.getStringExtra(EXTRA_BUTTON_TYPE)
            val toggledWidgetId = intent.getIntExtra(EXTRA_TOGGLED_WIDGET_ID, 0)
            val student = getStudent(
                sharedPref.getLong(getStudentWidgetKey(toggledWidgetId), 0),
                toggledWidgetId
            )
            val savedDate =
                LocalDate.ofEpochDay(sharedPref.getLong(getDateWidgetKey(toggledWidgetId), 0))
            val date = when (buttonType) {
                BUTTON_RESET -> now().nextOrSameSchoolDay
                BUTTON_NEXT -> savedDate.nextSchoolDay
                BUTTON_PREV -> savedDate.previousSchoolDay
                else -> now().nextOrSameSchoolDay
            }
            if (!buttonType.isNullOrBlank()) analytics.logEvent(
                "changed_timetable_widget_day",
                "button" to buttonType
            )
            updateWidget(context, toggledWidgetId, date, student)
        }
    }

    private fun onDelete(intent: Intent) {
        val appWidgetId = intent.getIntExtra(EXTRA_APPWIDGET_ID, 0)

        if (appWidgetId != 0) {
            with(sharedPref) {
                delete(getStudentWidgetKey(appWidgetId))
                delete(getDateWidgetKey(appWidgetId))
                delete(getThemeWidgetKey(appWidgetId))
                delete(getCurrentThemeWidgetKey(appWidgetId))
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun updateWidget(
        context: Context,
        appWidgetId: Int,
        date: LocalDate,
        student: Student?
    ) {
        val savedConfigureTheme = sharedPref.getLong(getThemeWidgetKey(appWidgetId), 0)
        val isSystemDarkMode =
            context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        var currentTheme = 0L
        var layoutId = R.layout.widget_timetable

        if (savedConfigureTheme == 1L || (savedConfigureTheme == 2L && isSystemDarkMode)) {
            currentTheme = 1L
            layoutId = R.layout.widget_timetable_dark
        }

        val nextNavIntent = createNavIntent(context, appWidgetId, appWidgetId, BUTTON_NEXT)
        val prevNavIntent = createNavIntent(context, -appWidgetId, appWidgetId, BUTTON_PREV)
        val resetNavIntent =
            createNavIntent(context, Int.MAX_VALUE - appWidgetId, appWidgetId, BUTTON_RESET)
        val adapterIntent = Intent(context, TimetableWidgetService::class.java)
            .apply {
                putExtra(EXTRA_APPWIDGET_ID, appWidgetId)
                //make Intent unique
                action = appWidgetId.toString()
            }
        val accountIntent = PendingIntent.getActivity(
            context, -Int.MAX_VALUE + appWidgetId,
            Intent(context, TimetableWidgetConfigureActivity::class.java).apply {
                addFlags(FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK)
                putExtra(EXTRA_APPWIDGET_ID, appWidgetId)
                putExtra(EXTRA_FROM_PROVIDER, true)
            }, FLAG_UPDATE_CURRENT
        )
        val appIntent = PendingIntent.getActivity(
            context,
            MainView.Section.TIMETABLE.id,
            MainActivity.getStartIntent(context, MainView.Section.TIMETABLE, true),
            FLAG_UPDATE_CURRENT
        )

        val remoteView = RemoteViews(context.packageName, layoutId).apply {
            setEmptyView(R.id.timetableWidgetList, R.id.timetableWidgetEmpty)
            setTextViewText(
                R.id.timetableWidgetDate,
                date.toFormattedString("EEEE, dd.MM").capitalise()
            )
            setTextViewText(
                R.id.timetableWidgetName,
                student?.nickOrName ?: context.getString(R.string.all_no_data)
            )

            student?.let {
                setImageViewBitmap(R.id.timetableWidgetAccount, context.createAvatarBitmap(it))
            }

            setRemoteAdapter(R.id.timetableWidgetList, adapterIntent)
            setOnClickPendingIntent(R.id.timetableWidgetNext, nextNavIntent)
            setOnClickPendingIntent(R.id.timetableWidgetPrev, prevNavIntent)
            setOnClickPendingIntent(R.id.timetableWidgetDate, resetNavIntent)
            setOnClickPendingIntent(R.id.timetableWidgetName, resetNavIntent)
            setOnClickPendingIntent(R.id.timetableWidgetAccount, accountIntent)
            setPendingIntentTemplate(R.id.timetableWidgetList, appIntent)
        }

        with(sharedPref) {
            putLong(getCurrentThemeWidgetKey(appWidgetId), currentTheme)
            putLong(getDateWidgetKey(appWidgetId), date.toEpochDay(), true)
        }

        with(appWidgetManager) {
            updateAppWidget(appWidgetId, remoteView)
            notifyAppWidgetViewDataChanged(appWidgetId, R.id.timetableWidgetList)
            Timber.d("TimetableWidgetProvider updated")
        }
    }

    private fun createNavIntent(
        context: Context,
        code: Int,
        appWidgetId: Int,
        buttonType: String
    ): PendingIntent {
        return PendingIntent.getBroadcast(
            context, code,
            Intent(context, TimetableWidgetProvider::class.java).apply {
                action = ACTION_APPWIDGET_UPDATE
                putExtra(EXTRA_BUTTON_TYPE, buttonType)
                putExtra(EXTRA_TOGGLED_WIDGET_ID, appWidgetId)
            }, FLAG_UPDATE_CURRENT
        )
    }

    private suspend fun getStudent(studentId: Long, appWidgetId: Int) = try {
        val students = studentRepository.getSavedStudents(false)
        val student = students.singleOrNull { it.student.id == studentId }?.student
        when {
            student != null -> student
            studentId != 0L && studentRepository.isCurrentStudentSet() -> {
                studentRepository.getCurrentStudent(false).also {
                    sharedPref.putLong(getStudentWidgetKey(appWidgetId), it.id)
                }
            }
            else -> null
        }
    } catch (e: Exception) {
        if (e.cause !is NoCurrentStudentException) {
            Timber.e(e, "An error has occurred in timetable widget provider")
        }
        null
    }

    private fun Context.createAvatarBitmap(student: Student): Bitmap {
        val avatarColor = if (student.avatarColor == -2937041L) {
            getCompatColor(R.color.colorPrimaryLight).toLong()
        } else {
            student.avatarColor
        }
        val avatarDrawable = createNameInitialsDrawable(student.nickOrName, avatarColor, 0.5f)

        val avatarBitmap =
            if (avatarDrawable.intrinsicWidth <= 0 || avatarDrawable.intrinsicHeight <= 0) {
                Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            } else {
                Bitmap.createBitmap(
                    avatarDrawable.intrinsicWidth,
                    avatarDrawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
            }

        val canvas = Canvas(avatarBitmap)
        avatarDrawable.setBounds(0, 0, canvas.width, canvas.height)
        avatarDrawable.draw(canvas)
        return avatarBitmap
    }
}
