package io.github.wulkanowy.ui.modules.timetablewidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Bitmap
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
                ACTION_APPWIDGET_UPDATE -> onWidgetUpdate(context, intent)
                ACTION_APPWIDGET_DELETED -> onWidgetDeleted(intent)
            }
        }
    }

    private suspend fun onWidgetUpdate(context: Context, intent: Intent) {
        val pressedButton = intent.getPressedButton()

        if (pressedButton == null) {
            val updatedWidgetIds = intent.getWidgetIds() ?: return
            updatedWidgetIds.forEach { updateWidgetLayout(context, it) }
        } else {
            val widgetId = intent.getToggledWidgetId() ?: return
            reportChangedDay(pressedButton)
            updateSavedWidgetDate(widgetId, pressedButton)
            updateWidgetLayout(context, widgetId)
        }
    }

    private fun Intent.getPressedButton(): String? {
        return getStringExtra(EXTRA_BUTTON_TYPE)
    }

    private fun Intent.getWidgetIds(): IntArray? {
        return getIntArrayExtra(EXTRA_APPWIDGET_IDS)
    }

    private fun Intent.getToggledWidgetId(): Int? {
        val toggledWidgetId = getIntExtra(EXTRA_TOGGLED_WIDGET_ID, INVALID_APPWIDGET_ID)
        return toggledWidgetId.takeIf { it != INVALID_APPWIDGET_ID }
    }

    private fun reportChangedDay(buttonType: String) {
        if (buttonType.isNotBlank()) {
            analytics.logEvent("changed_timetable_widget_day", "button" to buttonType)
        }
    }

    private fun updateSavedWidgetDate(widgetId: Int, buttonType: String) {
        val savedDate = getSavedWidgetDate(widgetId)
        val newDate = savedDate?.let { getNewDate(it, widgetId, buttonType) }
            ?: getWidgetDefaultDateToLoad(widgetId)
        setWidgetDate(widgetId, newDate)
    }

    private fun getSavedWidgetDate(widgetId: Int): LocalDate? {
        val epochDay = sharedPref.getLong(getDateWidgetKey(widgetId), 0)
        return if (epochDay == 0L) null else LocalDate.ofEpochDay(epochDay)
    }

    private fun getNewDate(
        currentDate: LocalDate,
        widgetId: Int,
        selectedButton: String
    ): LocalDate {
        return when (selectedButton) {
            BUTTON_NEXT -> currentDate.nextSchoolDay
            BUTTON_PREV -> currentDate.previousSchoolDay
            else -> getWidgetDefaultDateToLoad(widgetId)
        }
    }

    private fun setWidgetDate(widgetId: Int, dateToSet: LocalDate) {
        val widgetDateKey = getDateWidgetKey(widgetId)
        sharedPref.putLong(widgetDateKey, dateToSet.toEpochDay(), true)
    }

    private fun getWidgetDefaultDateToLoad(widgetId: Int): LocalDate {
        val lastLessonEndDateTime = getLastLessonDateTime(widgetId)

        val todayDate = LocalDate.now()
        val isLastLessonToday = lastLessonEndDateTime.toLocalDate() == todayDate
        val isEndOfLessons = LocalDateTime.now() > lastLessonEndDateTime

        return if (isLastLessonToday && isEndOfLessons) {
            todayDate.nextSchoolDay
        } else {
            todayDate.nextOrSameSchoolDay
        }
    }

    private fun getLastLessonDateTime(widgetId: Int): LocalDateTime {
        val lastLessonTimestamp = sharedPref
            .getLong(getTodayLastLessonEndDateTimeWidgetKey(widgetId), 0)
        return LocalDateTime.ofEpochSecond(lastLessonTimestamp, 0, ZoneOffset.UTC)
    }

    private suspend fun updateWidgetLayout(
        context: Context, widgetId: Int
    ) {
        val widgetRemoteViews = RemoteViews(context.packageName, R.layout.widget_timetable)

        // Apply the click action intent
        val appIntent = createPendingAppIntent(context)
        widgetRemoteViews.setPendingIntentTemplate(R.id.timetableWidgetList, appIntent)

        // Display saved date
        val date = getSavedWidgetDate(widgetId) ?: getWidgetDefaultDateToLoad(widgetId)
        val formattedDate = date.toFormattedString("EEE, dd.MM").capitalise()
        widgetRemoteViews.setTextViewText(R.id.timetableWidgetDate, formattedDate)

        // Apply intents to the date switcher buttons
        val nextNavIntent = createNavButtonIntent(context, widgetId, widgetId, BUTTON_NEXT)
        val prevNavIntent = createNavButtonIntent(context, -widgetId, widgetId, BUTTON_PREV)
        val resetNavIntent =
            createNavButtonIntent(context, Int.MAX_VALUE - widgetId, widgetId, BUTTON_RESET)
        widgetRemoteViews.run {
            setOnClickPendingIntent(R.id.timetableWidgetNext, nextNavIntent)
            setOnClickPendingIntent(R.id.timetableWidgetPrev, prevNavIntent)
            setOnClickPendingIntent(R.id.timetableWidgetDate, resetNavIntent)
        }

        // Setup the lesson list adapter
        val lessonListAdapterIntent = createLessonListAdapterIntent(context, widgetId)
        // --- Ensure the selected date is stored in the shared preferences,
        // --- on which the TimetableWidgetFactory relies
        setWidgetDate(widgetId, date)
        // ---
        widgetRemoteViews.apply {
            setEmptyView(R.id.timetableWidgetList, R.id.timetableWidgetEmpty)
            setRemoteAdapter(R.id.timetableWidgetList, lessonListAdapterIntent)
        }

        // Setup profile picture
        getWidgetStudent(widgetId)?.let { student ->
            setupAccountView(context, student, widgetRemoteViews, widgetId)
        }

        // Apply updates
        with(appWidgetManager) {
            partiallyUpdateAppWidget(widgetId, widgetRemoteViews)
            notifyAppWidgetViewDataChanged(widgetId, R.id.timetableWidgetList)
        }

        Timber.d("TimetableWidgetProvider updated")
    }

    private fun createPendingAppIntent(context: Context) = PendingIntent.getActivity(
        context, TIMETABLE_PENDING_INTENT_ID,
        SplashActivity.getStartIntent(context, Destination.Timetable()),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE
    )

    private fun createNavButtonIntent(
        context: Context, code: Int, appWidgetId: Int, buttonType: String
    ) = PendingIntent.getBroadcast(
        context, code, Intent(context, TimetableWidgetProvider::class.java).apply {
            action = ACTION_APPWIDGET_UPDATE
            putExtra(EXTRA_BUTTON_TYPE, buttonType)
            putExtra(EXTRA_TOGGLED_WIDGET_ID, appWidgetId)
        }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE
    )

    private fun createLessonListAdapterIntent(context: Context, widgetId: Int) =
        Intent(context, TimetableWidgetService::class.java).apply {
            putExtra(EXTRA_APPWIDGET_ID, widgetId)
            action = widgetId.toString() //make Intent unique
        }

    private suspend fun getWidgetStudent(widgetId: Int): Student? {
        val studentId = sharedPref.getLong(getStudentWidgetKey(widgetId), 0)
        return getStudent(studentId, widgetId)
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

    private fun setupAccountView(
        context: Context, student: Student, remoteViews: RemoteViews, widgetId: Int
    ) {
        val accountInitials = getAccountInitials(student.nickOrName)
        val accountPickerPendingIntent = createAccountPickerPendingIntent(context, widgetId)

        getAvatarBackgroundBitmap(context, student.avatarColor)?.let {
            remoteViews.setImageViewBitmap(R.id.timetableWidgetAccountBackground, it)
        }

        remoteViews.apply {
            setTextViewText(R.id.timetableWidgetAccountInitials, accountInitials)
            setOnClickPendingIntent(R.id.timetableWidgetAccount, accountPickerPendingIntent)
        }
    }

    private fun getAccountInitials(name: String): String {
        val firstLetters = name.split(" ").mapNotNull { it.firstOrNull() }
        return firstLetters.joinToString(separator = "").uppercase()
    }

    private fun createAccountPickerPendingIntent(context: Context, widgetId: Int) =
        PendingIntent.getActivity(
            context,
            -Int.MAX_VALUE + widgetId,
            Intent(context, TimetableWidgetConfigureActivity::class.java).apply {
                addFlags(FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK)
                putExtra(EXTRA_APPWIDGET_ID, widgetId)
                putExtra(EXTRA_FROM_PROVIDER, true)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE
        )

    private fun getAvatarBackgroundBitmap(context: Context, avatarColor: Long): Bitmap? {
        val avatarDrawableResource = R.drawable.background_timetable_widget_avatar
        return AppCompatResources.getDrawable(context, avatarDrawableResource)?.let { drawable ->
            val screenDensity = context.resources.displayMetrics.density
            val avatarSize = (48 * screenDensity).toInt()
            DrawableCompat.wrap(drawable).run {
                DrawableCompat.setTint(this, avatarColor.toInt())
                toBitmap(avatarSize, avatarSize)
            }
        }
    }

    private fun onWidgetDeleted(intent: Intent) {
        val deletedWidgetId = intent.getWidgetId()
        deleteWidgetPreferences(deletedWidgetId)
    }

    private fun Intent.getWidgetId(): Int {
        return getIntExtra(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID)
    }

    private fun deleteWidgetPreferences(widgetId: Int) {
        with(sharedPref) {
            delete(getStudentWidgetKey(widgetId))
            delete(getDateWidgetKey(widgetId))
        }
    }
}
