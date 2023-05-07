package io.github.wulkanowy.ui.modules.timetablewidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.widget.RemoteViews
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.SharedPrefProvider
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.exceptions.NoCurrentStudentException
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.services.widgets.TimetableWidgetService
import io.github.wulkanowy.ui.modules.Destination
import io.github.wulkanowy.ui.modules.splash.SplashActivity
import io.github.wulkanowy.utils.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject

@AndroidEntryPoint
class TimetableWidgetProvider : BroadcastReceiver() {

    @Inject
    lateinit var appWidgetManager: AppWidgetManager

    @Inject
    lateinit var studentRepository: StudentRepository

    @Inject
    lateinit var sharedPref: SharedPrefProvider

    @Inject
    lateinit var analytics: AnalyticsHelper

    companion object {

        private const val TIMETABLE_PENDING_INTENT_ID = 201

        private const val EXTRA_TOGGLED_WIDGET_ID = "extraToggledWidget"

        private const val EXTRA_BUTTON_TYPE = "extraButtonType"

        private const val BUTTON_NEXT = "buttonNext"

        private const val BUTTON_PREV = "buttonPrev"

        private const val BUTTON_RESET = "buttonReset"

        const val EXTRA_FROM_CONFIGURE = "extraFromConfigure"

        const val EXTRA_FROM_PROVIDER = "extraFromProvider"

        fun getDateWidgetKey(appWidgetId: Int) = "timetable_widget_date_$appWidgetId"

        fun getTodayLastLessonEndDateTimeWidgetKey(appWidgetId: Int) =
            "timetable_widget_today_last_lesson_end_date_time_$appWidgetId"

        fun getStudentWidgetKey(appWidgetId: Int) = "timetable_widget_student_$appWidgetId"
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context, intent: Intent) {
        GlobalScope.launch {
            when (intent.action) {
                ACTION_APPWIDGET_UPDATE -> onUpdate(context, intent)
                ACTION_APPWIDGET_DELETED -> onDelete(intent)
            }
        }
    }

    private suspend fun onUpdate(context: Context, intent: Intent) {
        if (intent.getStringExtra(EXTRA_BUTTON_TYPE) == null) {
            val isFromConfigure = intent.getBooleanExtra(EXTRA_FROM_CONFIGURE, false)
            val appWidgetIds = intent.getIntArrayExtra(EXTRA_APPWIDGET_IDS) ?: return

            appWidgetIds.forEach { appWidgetId ->
                val student =
                    getStudent(sharedPref.getLong(getStudentWidgetKey(appWidgetId), 0), appWidgetId)
                val savedDataEpochDay = sharedPref.getLong(getDateWidgetKey(appWidgetId), 0)

                val dateToLoad = if (isFromConfigure && savedDataEpochDay != 0L) {
                    LocalDate.ofEpochDay(savedDataEpochDay)
                } else {
                    getWidgetDefaultDateToLoad(appWidgetId)
                }

                updateWidget(context, appWidgetId, dateToLoad, student)
            }
        } else {
            val buttonType = intent.getStringExtra(EXTRA_BUTTON_TYPE)
            val toggledWidgetId = intent.getIntExtra(EXTRA_TOGGLED_WIDGET_ID, 0)
            val student = getStudent(
                sharedPref.getLong(getStudentWidgetKey(toggledWidgetId), 0), toggledWidgetId
            )
            val savedDate =
                LocalDate.ofEpochDay(sharedPref.getLong(getDateWidgetKey(toggledWidgetId), 0))
            val date = when (buttonType) {
                BUTTON_RESET -> getWidgetDefaultDateToLoad(toggledWidgetId)
                BUTTON_NEXT -> savedDate.nextSchoolDay
                BUTTON_PREV -> savedDate.previousSchoolDay
                else -> getWidgetDefaultDateToLoad(toggledWidgetId)
            }
            if (!buttonType.isNullOrBlank()) {
                analytics.logEvent(
                    "changed_timetable_widget_day", "button" to buttonType
                )
            }
            updateWidget(context, toggledWidgetId, date, student)
        }
    }

    private fun onDelete(intent: Intent) {
        val appWidgetId = intent.getIntExtra(EXTRA_APPWIDGET_ID, 0)

        if (appWidgetId != 0) {
            with(sharedPref) {
                delete(getStudentWidgetKey(appWidgetId))
                delete(getDateWidgetKey(appWidgetId))
            }
        }
    }

    private fun updateWidget(
        context: Context, appWidgetId: Int, date: LocalDate, student: Student?
    ) {
        val nextNavIntent = createNavIntent(context, appWidgetId, appWidgetId, BUTTON_NEXT)
        val prevNavIntent = createNavIntent(context, -appWidgetId, appWidgetId, BUTTON_PREV)
        val resetNavIntent =
            createNavIntent(context, Int.MAX_VALUE - appWidgetId, appWidgetId, BUTTON_RESET)
        val adapterIntent = Intent(context, TimetableWidgetService::class.java).apply {
            putExtra(EXTRA_APPWIDGET_ID, appWidgetId)
            action = appWidgetId.toString() //make Intent unique
        }
        val appIntent = PendingIntent.getActivity(
            context,
            TIMETABLE_PENDING_INTENT_ID,
            SplashActivity.getStartIntent(context, Destination.Timetable()),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE
        )

        val formattedDate = date.toFormattedString("EEE, dd.MM").capitalise()
        val remoteView = RemoteViews(context.packageName, R.layout.widget_timetable).apply {
            setEmptyView(R.id.timetableWidgetList, R.id.timetableWidgetEmpty)
            setTextViewText(R.id.timetableWidgetDate, formattedDate)
            setRemoteAdapter(R.id.timetableWidgetList, adapterIntent)
            setOnClickPendingIntent(R.id.timetableWidgetNext, nextNavIntent)
            setOnClickPendingIntent(R.id.timetableWidgetPrev, prevNavIntent)
            setOnClickPendingIntent(R.id.timetableWidgetDate, resetNavIntent)
            setPendingIntentTemplate(R.id.timetableWidgetList, appIntent)
        }

        student?.let {
            setupAccountView(context, student, remoteView, appWidgetId)
        }

        with(sharedPref) {
            putLong(getDateWidgetKey(appWidgetId), date.toEpochDay(), true)
        }

        with(appWidgetManager) {
            partiallyUpdateAppWidget(appWidgetId, remoteView)
            notifyAppWidgetViewDataChanged(appWidgetId, R.id.timetableWidgetList)
        }

        Timber.d("TimetableWidgetProvider updated")
    }

    private fun createNavIntent(
        context: Context, code: Int, appWidgetId: Int, buttonType: String
    ) = PendingIntent.getBroadcast(
        context, code, Intent(context, TimetableWidgetProvider::class.java).apply {
            action = ACTION_APPWIDGET_UPDATE
            putExtra(EXTRA_BUTTON_TYPE, buttonType)
            putExtra(EXTRA_TOGGLED_WIDGET_ID, appWidgetId)
        }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE
    )

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

    private fun getWidgetDefaultDateToLoad(appWidgetId: Int): LocalDate {
        val lastLessonEndTimestamp =
            sharedPref.getLong(getTodayLastLessonEndDateTimeWidgetKey(appWidgetId), 0)
        val lastLessonEndDateTime =
            LocalDateTime.ofEpochSecond(lastLessonEndTimestamp, 0, ZoneOffset.UTC)

        val todayDate = LocalDate.now()
        val isLastLessonEndDateNow = lastLessonEndDateTime.toLocalDate() == todayDate
        val isLastLessonEndDateAfterNowTime = LocalDateTime.now() > lastLessonEndDateTime

        return if (isLastLessonEndDateNow && isLastLessonEndDateAfterNowTime) {
            todayDate.nextSchoolDay
        } else {
            todayDate.nextOrSameSchoolDay
        }
    }

    private fun setupAccountView(
        context: Context,
        student: Student,
        remoteViews: RemoteViews,
        appWidgetId: Int
    ) {
        val accountInitials = student.nickOrName
            .split(" ")
            .mapNotNull { it.firstOrNull() }.take(2)
            .joinToString(separator = "").uppercase()

        val accountPickerIntent = PendingIntent.getActivity(
            context,
            -Int.MAX_VALUE + appWidgetId,
            Intent(context, TimetableWidgetConfigureActivity::class.java).apply {
                addFlags(FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK)
                putExtra(EXTRA_APPWIDGET_ID, appWidgetId)
                putExtra(EXTRA_FROM_PROVIDER, true)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE
        )

        // Create background bitmap
        val avatarDrawableResource = R.drawable.background_timetable_widget_avatar
        AppCompatResources.getDrawable(context, avatarDrawableResource)?.let { drawable ->
            val screenDensity = context.resources.displayMetrics.density
            val avatarSize = (48 * screenDensity).toInt()
            val backgroundBitmap = DrawableCompat.wrap(drawable).run {
                DrawableCompat.setTint(this, student.avatarColor.toInt())
                toBitmap(avatarSize, avatarSize)
            }
            remoteViews.setImageViewBitmap(R.id.timetableWidgetAccountBackground, backgroundBitmap)
        }

        remoteViews.apply {
            setTextViewText(R.id.timetableWidgetAccountInitials, accountInitials)
            setOnClickPendingIntent(R.id.timetableWidgetAccount, accountPickerIntent)
        }
    }
}
