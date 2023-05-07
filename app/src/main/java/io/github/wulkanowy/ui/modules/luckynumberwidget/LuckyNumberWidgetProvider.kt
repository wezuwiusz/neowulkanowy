package io.github.wulkanowy.ui.modules.luckynumberwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.View
import android.widget.RemoteViews
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.Resource
import io.github.wulkanowy.data.dataOrNull
import io.github.wulkanowy.data.db.SharedPrefProvider
import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.data.repositories.LuckyNumberRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.toFirstResult
import io.github.wulkanowy.ui.modules.Destination
import io.github.wulkanowy.ui.modules.splash.SplashActivity
import io.github.wulkanowy.utils.PendingIntentCompat
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LuckyNumberWidgetProvider : AppWidgetProvider() {

    @Inject
    lateinit var studentRepository: StudentRepository

    @Inject
    lateinit var luckyNumberRepository: LuckyNumberRepository

    @Inject
    lateinit var sharedPref: SharedPrefProvider

    companion object {
        private const val LUCKY_NUMBER_WIDGET_MAX_SIZE = 196

        private const val LUCKY_NUMBER_PENDING_INTENT_ID = 300
        private const val LUCKY_NUMBER_HISTORY_PENDING_INTENT_ID = 301

        fun getStudentWidgetKey(appWidgetId: Int) = "lucky_number_widget_student_$appWidgetId"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        val appIntent = PendingIntent.getActivity(
            context,
            LUCKY_NUMBER_PENDING_INTENT_ID,
            SplashActivity.getStartIntent(context, Destination.LuckyNumber),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE
        )

        val historyIntent = PendingIntent.getActivity(
            context,
            LUCKY_NUMBER_HISTORY_PENDING_INTENT_ID,
            SplashActivity.getStartIntent(context, Destination.LuckyNumberHistory),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE
        )

        appWidgetIds?.forEach { widgetId ->
            val studentId = sharedPref.getLong(getStudentWidgetKey(widgetId), 0)
            val luckyNumberResource = getLuckyNumber(studentId, widgetId)
            val luckyNumber = luckyNumberResource.dataOrNull?.luckyNumber?.toString()
            val remoteView = RemoteViews(context.packageName, R.layout.widget_luckynumber)
                .apply {
                    setTextViewText(R.id.luckyNumberWidgetValue, luckyNumber ?: "-")
                    setOnClickPendingIntent(R.id.luckyNumberWidgetContainer, appIntent)
                    setOnClickPendingIntent(R.id.luckyNumberWidgetHistoryButton, historyIntent)
                }

            resizeWidget(context, appWidgetManager.getAppWidgetOptions(widgetId), remoteView)
            appWidgetManager.updateAppWidget(widgetId, remoteView)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)

        if (context == null || newOptions == null || appWidgetManager == null) {
            return
        }

        val remoteView = RemoteViews(context.packageName, R.layout.widget_luckynumber)
        resizeWidget(context, newOptions, remoteView)
        appWidgetManager.partiallyUpdateAppWidget(appWidgetId, remoteView)
    }

    private fun resizeWidget(context: Context, options: Bundle, remoteViews: RemoteViews) {
        val (width, height) = options.getWidgetSize(context)
        val size = minOf(width, height, LUCKY_NUMBER_WIDGET_MAX_SIZE).toFloat()
        resizeWidgetContents(size, remoteViews)
        Timber.v("LuckyNumberWidget resized: ${width}x${height} ($size)")
    }

    private fun resizeWidgetContents(size: Float, remoteViews: RemoteViews) {
        var historyButtonVisibility = View.VISIBLE
        var luckyNumberTextSize = 72f

        if (size < 150) {
            luckyNumberTextSize = 44f
            historyButtonVisibility = View.GONE
        }
        if (size < 75) {
            luckyNumberTextSize = 26f
        }

        remoteViews.apply {
            setTextViewTextSize(R.id.luckyNumberWidgetValue, COMPLEX_UNIT_SP, luckyNumberTextSize)
            setViewVisibility(R.id.luckyNumberWidgetHistoryButton, historyButtonVisibility)
        }
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        appWidgetIds?.forEach { appWidgetId ->
            sharedPref.delete(getStudentWidgetKey(appWidgetId))
        }
    }

    private fun getLuckyNumber(studentId: Long, appWidgetId: Int) = runBlocking {
        try {
            val students = studentRepository.getSavedStudents()
            val student = students.singleOrNull { it.student.id == studentId }?.student
            val currentStudent = when {
                student != null -> student
                studentId != 0L && studentRepository.isCurrentStudentSet() -> {
                    studentRepository.getCurrentStudent(false).also {
                        sharedPref.putLong(getStudentWidgetKey(appWidgetId), it.id)
                    }
                }
                else -> null
            }

            if (currentStudent != null) {
                luckyNumberRepository.getLuckyNumber(currentStudent, forceRefresh = false)
                    .toFirstResult()
            } else {
                Resource.Success<LuckyNumber?>(null)
            }
        } catch (e: Exception) {
            Timber.e(e, "An error has occurred in lucky number provider")
            Resource.Error(e)
        }
    }

    private fun Bundle.getWidgetSize(context: Context): Pair<Int, Int> {
        val minWidth = getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
        val maxWidth = getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH)
        val minHeight = getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
        val maxHeight = getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)

        val orientation = context.resources.configuration.orientation
        val isPortrait = orientation == Configuration.ORIENTATION_PORTRAIT

        return if (isPortrait) {
            minWidth to maxHeight
        } else {
            maxWidth to minHeight
        }
    }
}
