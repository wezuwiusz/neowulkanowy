package io.github.wulkanowy.ui.modules.luckynumberwidget

import android.annotation.TargetApi
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_DELETED
import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED
import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_OPTIONS
import android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT
import android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.RemoteViews
import dagger.android.AndroidInjection
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.SharedPrefProvider
import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.data.exceptions.NoCurrentStudentException
import io.github.wulkanowy.data.repositories.luckynumber.LuckyNumberRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.SchedulersProvider
import io.reactivex.Maybe
import timber.log.Timber
import javax.inject.Inject

class LuckyNumberWidgetProvider : BroadcastReceiver() {

    @Inject
    lateinit var studentRepository: StudentRepository

    @Inject
    lateinit var semesterRepository: SemesterRepository

    @Inject
    lateinit var luckyNumberRepository: LuckyNumberRepository

    @Inject
    lateinit var schedulers: SchedulersProvider

    @Inject
    lateinit var appWidgetManager: AppWidgetManager

    @Inject
    lateinit var sharedPref: SharedPrefProvider

    companion object {

        fun getStudentWidgetKey(appWidgetId: Int) = "lucky_number_widget_student_$appWidgetId"

        fun getThemeWidgetKey(appWidgetId: Int) = "lucky_number_widget_theme_$appWidgetId"
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)
        when (intent.action) {
            ACTION_APPWIDGET_UPDATE -> onUpdate(context, intent)
            ACTION_APPWIDGET_DELETED -> onDelete(intent)
            ACTION_APPWIDGET_OPTIONS_CHANGED -> onOptionsChange(context, intent)
        }
    }

    private fun onUpdate(context: Context, intent: Intent) {
        intent.getIntArrayExtra(EXTRA_APPWIDGET_IDS)?.forEach { appWidgetId ->
            val savedTheme = sharedPref.getLong(getThemeWidgetKey(appWidgetId), 0)
            val layoutId = if (savedTheme == 0L) R.layout.widget_luckynumber else R.layout.widget_luckynumber_dark

            val luckyNumber = getLuckyNumber(sharedPref.getLong(getStudentWidgetKey(appWidgetId), 0), appWidgetId)
            val appIntent = PendingIntent.getActivity(context, MainView.Section.LUCKY_NUMBER.id,
                MainActivity.getStartIntent(context, MainView.Section.LUCKY_NUMBER, true), FLAG_UPDATE_CURRENT)

            val remoteView = RemoteViews(context.packageName, layoutId).apply {
                setTextViewText(R.id.luckyNumberWidgetNumber, luckyNumber?.luckyNumber?.toString() ?: "#")
                setOnClickPendingIntent(R.id.luckyNumberWidgetContainer, appIntent)
            }

            setStyles(remoteView, intent)
            appWidgetManager.updateAppWidget(appWidgetId, remoteView)
        }
    }

    private fun onDelete(intent: Intent) {
        val appWidgetId = intent.getIntExtra(EXTRA_APPWIDGET_ID, 0)
        if (appWidgetId != 0) sharedPref.delete(getStudentWidgetKey(appWidgetId))
    }

    private fun getLuckyNumber(studentId: Long, appWidgetId: Int): LuckyNumber? {
        return try {
            studentRepository.isStudentSaved()
                .filter { true }
                .flatMap { studentRepository.getSavedStudents().toMaybe() }
                .flatMap { students ->
                    val student = students.singleOrNull { student -> student.id == studentId }
                    when {
                        student != null -> Maybe.just(student)
                        studentId != 0L -> {
                            studentRepository.isCurrentStudentSet()
                                .filter { true }
                                .flatMap { studentRepository.getCurrentStudent(false).toMaybe() }
                                .doOnSuccess { sharedPref.putLong(getStudentWidgetKey(appWidgetId), it.id) }
                        }
                        else -> Maybe.empty()
                    }
                }
                .flatMap { semesterRepository.getCurrentSemester(it).toMaybe() }
                .flatMap { luckyNumberRepository.getLuckyNumber(it) }
                .subscribeOn(schedulers.backgroundThread)
                .blockingGet()
        } catch (e: Exception) {
            if (e.cause !is NoCurrentStudentException) {
                Timber.e(e, "An error has occurred in lucky number provider")
            }
            null
        }
    }

    private fun onOptionsChange(context: Context, intent: Intent) {
        intent.extras?.getInt(EXTRA_APPWIDGET_ID)?.let { appWidgetId ->
            val savedTheme = sharedPref.getLong(getThemeWidgetKey(appWidgetId), 0)
            val layoutId = if (savedTheme == 0L) R.layout.widget_luckynumber else R.layout.widget_luckynumber_dark

            val remoteView = RemoteViews(context.packageName, layoutId)

            setStyles(remoteView, intent)
            appWidgetManager.updateAppWidget(appWidgetId, remoteView)
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun setStyles(views: RemoteViews, intent: Intent) {
        val options = intent.extras?.getBundle(EXTRA_APPWIDGET_OPTIONS)

        val maxWidth = options?.getInt(OPTION_APPWIDGET_MAX_WIDTH) ?: 150
        val maxHeight = options?.getInt(OPTION_APPWIDGET_MAX_HEIGHT) ?: 40

        Timber.d("New lucky number widget measurement: %dx%d", maxWidth, maxHeight)

        when {
            // 1x1
            maxWidth < 150 && maxHeight < 110 -> {
                Timber.d("Lucky number widget size: 1x1")
                with(views) {
                    setViewVisibility(R.id.luckyNumberWidgetImageTop, GONE)
                    setViewVisibility(R.id.luckyNumberWidgetImageLeft, GONE)
                    setViewVisibility(R.id.luckyNumberWidgetTitle, GONE)
                    setViewVisibility(R.id.luckyNumberWidgetNumber, VISIBLE)
                }
            }
            // 1x2
            maxWidth < 150 && maxHeight > 110 -> {
                Timber.d("Lucky number widget size: 1x2")
                with(views) {
                    setViewVisibility(R.id.luckyNumberWidgetImageTop, VISIBLE)
                    setViewVisibility(R.id.luckyNumberWidgetImageLeft, GONE)
                    setViewVisibility(R.id.luckyNumberWidgetTitle, GONE)
                    setViewVisibility(R.id.luckyNumberWidgetNumber, VISIBLE)
                }
            }
            // 2x1
            maxWidth >= 150 && maxHeight <= 110 -> {
                Timber.d("Lucky number widget size: 2x1")
                with(views) {
                    setViewVisibility(R.id.luckyNumberWidgetImageTop, GONE)
                    setViewVisibility(R.id.luckyNumberWidgetImageLeft, VISIBLE)
                    setViewVisibility(R.id.luckyNumberWidgetTitle, GONE)
                    setViewVisibility(R.id.luckyNumberWidgetNumber, VISIBLE)
                }
            }
            // 2x2 and bigger
            else -> {
                Timber.d("Lucky number widget size: 2x2 and bigger")
                with(views) {
                    setViewVisibility(R.id.luckyNumberWidgetImageTop, GONE)
                    setViewVisibility(R.id.luckyNumberWidgetImageLeft, GONE)
                    setViewVisibility(R.id.luckyNumberWidgetTitle, VISIBLE)
                    setViewVisibility(R.id.luckyNumberWidgetNumber, VISIBLE)
                }
            }
        }
    }
}
