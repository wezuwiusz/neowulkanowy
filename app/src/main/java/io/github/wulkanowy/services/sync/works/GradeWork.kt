package io.github.wulkanowy.services.sync.works

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.DEFAULT_ALL
import androidx.core.app.NotificationCompat.PRIORITY_HIGH
import androidx.core.app.NotificationManagerCompat
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.grade.GradeRepository
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.services.sync.channels.NewEntriesChannel
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainActivity.Companion.EXTRA_START_MENU
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.getCompatColor
import io.reactivex.Completable
import javax.inject.Inject
import kotlin.random.Random

class GradeWork @Inject constructor(
    private val context: Context,
    private val notificationManager: NotificationManagerCompat,
    private val gradeRepository: GradeRepository,
    private val preferencesRepository: PreferencesRepository
) : Work {

    override fun create(student: Student, semester: Semester): Completable {
        return gradeRepository.getGrades(student, semester, true, preferencesRepository.isNotificationsEnable)
            .flatMap { gradeRepository.getNotNotifiedGrades(semester) }
            .flatMapCompletable {
                if (it.isNotEmpty()) notify(it)
                gradeRepository.updateGrades(it.onEach { grade -> grade.isNotified = true })
            }
    }

    private fun notify(grades: List<Grade>) {
        notificationManager.notify(Random.nextInt(Int.MAX_VALUE), NotificationCompat.Builder(context, NewEntriesChannel.CHANNEL_ID)
            .setContentTitle(context.resources.getQuantityString(R.plurals.grade_new_items, grades.size, grades.size))
            .setContentText(context.resources.getQuantityString(R.plurals.grade_notify_new_items, grades.size, grades.size))
            .setSmallIcon(R.drawable.ic_stat_notify_grade)
            .setAutoCancel(true)
            .setPriority(PRIORITY_HIGH)
            .setDefaults(DEFAULT_ALL)
            .setColor(context.getCompatColor(R.color.colorPrimary))
            .setContentIntent(
                PendingIntent.getActivity(context, 0,
                    MainActivity.getStartIntent(context).putExtra(EXTRA_START_MENU, MainView.MenuView.GRADE), FLAG_UPDATE_CURRENT))
            .setStyle(NotificationCompat.InboxStyle().run {
                setSummaryText(context.resources.getQuantityString(R.plurals.grade_number_item, grades.size, grades.size))
                grades.forEach { addLine("${it.subject}: ${it.entry}") }
                this
            })
            .build()
        )
    }
}

