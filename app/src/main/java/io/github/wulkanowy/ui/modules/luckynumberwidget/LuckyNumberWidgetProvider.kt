package io.github.wulkanowy.ui.modules.luckynumberwidget

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT
import android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.RemoteViews
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.SharedPrefProvider
import io.github.wulkanowy.data.exceptions.NoCurrentStudentException
import io.github.wulkanowy.data.repositories.LuckyNumberRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.toFirstResult
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

        fun getStudentWidgetKey(appWidgetId: Int) = "lucky_number_widget_student_$appWidgetId"

        fun getThemeWidgetKey(appWidgetId: Int) = "lucky_number_widget_theme_$appWidgetId"

        fun getHeightWidgetKey(appWidgetId: Int) = "lucky_number_widget_height_$appWidgetId"

        fun getWidthWidgetKey(appWidgetId: Int) = "lucky_number_widget_width_$appWidgetId"
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray?) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        appWidgetIds?.forEach { appWidgetId ->

            val luckyNumber = getLuckyNumber(sharedPref.getLong(getStudentWidgetKey(appWidgetId), 0), appWidgetId)
            val appIntent = PendingIntent.getActivity(context, MainView.Section.LUCKY_NUMBER.id,
                MainActivity.getStartIntent(context, MainView.Section.LUCKY_NUMBER, true), FLAG_UPDATE_CURRENT)

            val remoteView = RemoteViews(context.packageName, getCorrectLayoutId(appWidgetId, context)).apply {
                setTextViewText(R.id.luckyNumberWidgetNumber, luckyNumber?.luckyNumber?.toString() ?: "#")
                setOnClickPendingIntent(R.id.luckyNumberWidgetContainer, appIntent)
            }

            setStyles(remoteView, appWidgetId)
            appWidgetManager.updateAppWidget(appWidgetId, remoteView)
        }
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        appWidgetIds?.forEach { appWidgetId ->
            with(sharedPref) {
                delete(getHeightWidgetKey(appWidgetId))
                delete(getStudentWidgetKey(appWidgetId))
                delete(getThemeWidgetKey(appWidgetId))
                delete(getWidthWidgetKey(appWidgetId))
            }
        }
    }

    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle?) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)

        val remoteView = RemoteViews(context.packageName, getCorrectLayoutId(appWidgetId, context))

        setStyles(remoteView, appWidgetId, newOptions)
        appWidgetManager.updateAppWidget(appWidgetId, remoteView)
    }

    private fun setStyles(views: RemoteViews, appWidgetId: Int, options: Bundle? = null) {
        val width = options?.getInt(OPTION_APPWIDGET_MIN_WIDTH) ?: sharedPref.getLong(getWidthWidgetKey(appWidgetId), 74).toInt()
        val height = options?.getInt(OPTION_APPWIDGET_MAX_HEIGHT) ?: sharedPref.getLong(getHeightWidgetKey(appWidgetId), 74).toInt()

        with(sharedPref) {
            putLong(getWidthWidgetKey(appWidgetId), width.toLong())
            putLong(getHeightWidgetKey(appWidgetId), height.toLong())
        }

        val rows = getCellsForSize(height)
        val cols = getCellsForSize(width)

        Timber.d("New lucky number widget measurement: %dx%d", width, height)
        Timber.d("Widget size: $cols x $rows")

        when {
            1 == cols && 1 == rows -> views.setVisibility(imageTop = false, imageLeft = false)
            1 == cols && 1 < rows -> views.setVisibility(imageTop = true, imageLeft = false)
            1 < cols && 1 == rows -> views.setVisibility(imageTop = false, imageLeft = true)
            1 == cols && 1 == rows -> views.setVisibility(imageTop = true, imageLeft = false)
            2 == cols && 1 == rows -> views.setVisibility(imageTop = false, imageLeft = true)
            else -> views.setVisibility(imageTop = false, imageLeft = false, title = true)
        }
    }

    private fun RemoteViews.setVisibility(imageTop: Boolean, imageLeft: Boolean, title: Boolean = false) {
        setViewVisibility(R.id.luckyNumberWidgetImageTop, if (imageTop) VISIBLE else GONE)
        setViewVisibility(R.id.luckyNumberWidgetImageLeft, if (imageLeft) VISIBLE else GONE)
        setViewVisibility(R.id.luckyNumberWidgetTitle, if (title) VISIBLE else GONE)
        setViewVisibility(R.id.luckyNumberWidgetNumber, VISIBLE)
    }

    private fun getCellsForSize(size: Int): Int {
        var n = 2
        while (74 * n - 30 < size) ++n
        return n - 1
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

            currentStudent?.let {
                luckyNumberRepository.getLuckyNumber(it, false).toFirstResult().data
            }
        } catch (e: Exception) {
            if (e.cause !is NoCurrentStudentException) {
                Timber.e(e, "An error has occurred in lucky number provider")
            }
            null
        }
    }

    private fun getCorrectLayoutId(appWidgetId: Int, context: Context): Int {
        val savedTheme = sharedPref.getLong(getThemeWidgetKey(appWidgetId), 0)
        val isSystemDarkMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        return if (savedTheme == 1L || (savedTheme == 2L && isSystemDarkMode)) {
            R.layout.widget_luckynumber_dark
        } else {
            R.layout.widget_luckynumber
        }
    }
}
