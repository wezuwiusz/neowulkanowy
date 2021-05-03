package io.github.wulkanowy.services.sync.works

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.DEFAULT_ALL
import androidx.core.app.NotificationCompat.PRIORITY_HIGH
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.GradeSummary
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.GradeRepository
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.services.sync.channels.NewGradesChannel
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.getCompatBitmap
import io.github.wulkanowy.utils.getCompatColor
import io.github.wulkanowy.utils.waitForResult
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlin.random.Random

class GradeWork @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationManager: NotificationManagerCompat,
    private val gradeRepository: GradeRepository,
    private val preferencesRepository: PreferencesRepository
) : Work {

    override suspend fun doWork(student: Student, semester: Semester) {
        gradeRepository.getGrades(student, semester, true, preferencesRepository.isNotificationsEnable).waitForResult()

        gradeRepository.getNotNotifiedGrades(semester).first().let {
            if (it.isNotEmpty()) notifyDetails(it)
            gradeRepository.updateGrades(it.onEach { grade -> grade.isNotified = true })
        }

        gradeRepository.getNotNotifiedPredictedGrades(semester).first().let {
            if (it.isNotEmpty()) notifyPredicted(it)
            gradeRepository.updateGradesSummary(it.onEach { grade -> grade.isPredictedGradeNotified = true })
        }

        gradeRepository.getNotNotifiedFinalGrades(semester).first().let {
            if (it.isNotEmpty()) notifyFinal(it)
            gradeRepository.updateGradesSummary(it.onEach { grade -> grade.isFinalGradeNotified = true })
        }
    }

    private fun notifyDetails(grades: List<Grade>) {
        notificationManager.notify(Random.nextInt(Int.MAX_VALUE), getNotificationBuilder()
            .setContentTitle(context.resources.getQuantityString(R.plurals.grade_new_items, grades.size, grades.size))
            .setContentText(context.resources.getQuantityString(R.plurals.grade_notify_new_items, grades.size, grades.size))
            .setStyle(NotificationCompat.InboxStyle().run {
                setSummaryText(context.resources.getQuantityString(R.plurals.grade_number_item, grades.size, grades.size))
                grades.forEach { addLine("${it.subject}: ${it.entry}") }
                this
            })
            .build()
        )
    }

    private fun notifyPredicted(gradesSummary: List<GradeSummary>) {
        notificationManager.notify(Random.nextInt(Int.MAX_VALUE), getNotificationBuilder()
            .setContentTitle(context.resources.getQuantityString(R.plurals.grade_new_items_predicted, gradesSummary.size, gradesSummary.size))
            .setContentText(context.resources.getQuantityString(R.plurals.grade_notify_new_items_predicted, gradesSummary.size, gradesSummary.size))
            .setStyle(NotificationCompat.InboxStyle().run {
                setSummaryText(context.resources.getQuantityString(R.plurals.grade_number_item, gradesSummary.size, gradesSummary.size))
                gradesSummary.forEach { addLine("${it.subject}: ${it.predictedGrade}") }
                this
            })
            .build()
        )
    }

    private fun notifyFinal(gradesSummary: List<GradeSummary>) {
        notificationManager.notify(Random.nextInt(Int.MAX_VALUE), getNotificationBuilder()
            .setContentTitle(context.resources.getQuantityString(R.plurals.grade_new_items_final, gradesSummary.size, gradesSummary.size))
            .setContentText(context.resources.getQuantityString(R.plurals.grade_notify_new_items_final, gradesSummary.size, gradesSummary.size))
            .setStyle(NotificationCompat.InboxStyle().run {
                setSummaryText(context.resources.getQuantityString(R.plurals.grade_number_item, gradesSummary.size, gradesSummary.size))
                gradesSummary.forEach { addLine("${it.subject}: ${it.finalGrade}") }
                this
            })
            .build()
        )
    }

    private fun getNotificationBuilder(): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, NewGradesChannel.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_all)
            .setLargeIcon(
                context.getCompatBitmap(R.drawable.ic_stat_grade, R.color.colorPrimary)
            )
            .setAutoCancel(true)
            .setPriority(PRIORITY_HIGH)
            .setDefaults(DEFAULT_ALL)
            .setColor(context.getCompatColor(R.color.colorPrimary))
            .setContentIntent(
                PendingIntent.getActivity(
                    context, MainView.Section.GRADE.id,
                    MainActivity.getStartIntent(context, MainView.Section.GRADE, true),
                    FLAG_UPDATE_CURRENT
                )
            )
    }
}
