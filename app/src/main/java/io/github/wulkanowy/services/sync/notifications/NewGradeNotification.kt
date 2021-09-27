package io.github.wulkanowy.services.sync.notifications

import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.GradeSummary
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.MultipleNotificationsData
import io.github.wulkanowy.ui.modules.main.MainView
import javax.inject.Inject

class NewGradeNotification @Inject constructor(
    private val appNotificationManager: AppNotificationManager
) {

    suspend fun notifyDetails(items: List<Grade>, student: Student) {
        val notification = MultipleNotificationsData(
            type = NotificationType.NEW_GRADE_DETAILS,
            icon = R.drawable.ic_stat_grade,
            titleStringRes = R.plurals.grade_new_items,
            contentStringRes = R.plurals.grade_notify_new_items,
            summaryStringRes = R.plurals.grade_number_item,
            startMenu = MainView.Section.GRADE,
            lines = items.map {
                "${it.subject}: ${it.entry}"
            }
        )

        appNotificationManager.sendNotification(notification, student)
    }

    suspend fun notifyPredicted(items: List<GradeSummary>, student: Student) {
        val notification = MultipleNotificationsData(
            type = NotificationType.NEW_GRADE_PREDICTED,
            icon = R.drawable.ic_stat_grade,
            titleStringRes = R.plurals.grade_new_items_predicted,
            contentStringRes = R.plurals.grade_notify_new_items_predicted,
            summaryStringRes = R.plurals.grade_number_item,
            startMenu = MainView.Section.GRADE,
            lines = items.map {
                "${it.subject}: ${it.predictedGrade}"
            }
        )

        appNotificationManager.sendNotification(notification, student)
    }

    suspend fun notifyFinal(items: List<GradeSummary>, student: Student) {
        val notification = MultipleNotificationsData(
            type = NotificationType.NEW_GRADE_FINAL,
            icon = R.drawable.ic_stat_grade,
            titleStringRes = R.plurals.grade_new_items_final,
            contentStringRes = R.plurals.grade_notify_new_items_final,
            summaryStringRes = R.plurals.grade_number_item,
            startMenu = MainView.Section.GRADE,
            lines = items.map {
                "${it.subject}: ${it.finalGrade}"
            }
        )

        appNotificationManager.sendNotification(notification, student)
    }
}
