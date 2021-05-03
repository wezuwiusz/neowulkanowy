package io.github.wulkanowy.services.sync.works

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.HomeworkRepository
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.services.sync.channels.NewHomeworkChannel
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.getCompatBitmap
import io.github.wulkanowy.utils.getCompatColor
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.sunday
import io.github.wulkanowy.utils.waitForResult
import kotlinx.coroutines.flow.first
import java.time.LocalDate.now
import javax.inject.Inject
import kotlin.random.Random

class HomeworkWork @Inject constructor(
    @ApplicationContext private val context: Context,
    private val homeworkRepository: HomeworkRepository,
    private val notificationManager: NotificationManagerCompat,
    private val preferencesRepository: PreferencesRepository
) : Work {

    override suspend fun doWork(student: Student, semester: Semester) {
        homeworkRepository.getHomework(
            student = student,
            semester = semester,
            start = now().monday,
            end = now().sunday,
            forceRefresh = true,
            notify = preferencesRepository.isNotificationsEnable
        ).waitForResult()

        homeworkRepository.getNotNotifiedHomework(semester, now().monday, now().sunday).first()
            .let {
                if (it.isNotEmpty()) notify(it)

                homeworkRepository.updateHomework(it.onEach { homework ->
                    homework.isNotified = true
                })
            }
    }

    private fun notify(homework: List<Homework>) {
        notificationManager.notify(
            Random.nextInt(Int.MAX_VALUE),
            NotificationCompat.Builder(context, NewHomeworkChannel.CHANNEL_ID)
                .setContentTitle(
                    context.resources.getQuantityString(
                        R.plurals.homework_notify_new_item_title, homework.size, homework.size
                    )
                )
                .setSmallIcon(R.drawable.ic_stat_all)
                .setLargeIcon(
                    context.getCompatBitmap(R.drawable.ic_more_homework, R.color.colorPrimary)
                )
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(context.getCompatColor(R.color.colorPrimary))
                .setContentIntent(
                    PendingIntent.getActivity(
                        context, MainView.Section.MESSAGE.id,
                        MainActivity.getStartIntent(context, MainView.Section.HOMEWORK, true),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
                .setStyle(NotificationCompat.InboxStyle().run {
                    setSummaryText(
                        context.resources.getQuantityString(
                            R.plurals.homework_number_item, homework.size, homework.size
                        )
                    )
                    homework.forEach { addLine("${it.subject}: ${it.content}") }
                    this
                })
                .build()
        )
    }
}
