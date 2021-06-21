package io.github.wulkanowy.ui.modules.debug.notification

import io.github.wulkanowy.R
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.services.sync.notifications.NewConferenceNotification
import io.github.wulkanowy.services.sync.notifications.NewExamNotification
import io.github.wulkanowy.services.sync.notifications.NewGradeNotification
import io.github.wulkanowy.services.sync.notifications.NewHomeworkNotification
import io.github.wulkanowy.services.sync.notifications.NewLuckyNumberNotification
import io.github.wulkanowy.services.sync.notifications.NewMessageNotification
import io.github.wulkanowy.services.sync.notifications.NewNoteNotification
import io.github.wulkanowy.services.sync.notifications.NewSchoolAnnouncementNotification
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.ui.modules.debug.notification.mock.debugConferenceItems
import io.github.wulkanowy.ui.modules.debug.notification.mock.debugExamItems
import io.github.wulkanowy.ui.modules.debug.notification.mock.debugGradeDetailsItems
import io.github.wulkanowy.ui.modules.debug.notification.mock.debugGradeSummaryItems
import io.github.wulkanowy.ui.modules.debug.notification.mock.debugHomeworkItems
import io.github.wulkanowy.ui.modules.debug.notification.mock.debugLuckyNumber
import io.github.wulkanowy.ui.modules.debug.notification.mock.debugMessageItems
import io.github.wulkanowy.ui.modules.debug.notification.mock.debugNoteItems
import io.github.wulkanowy.ui.modules.debug.notification.mock.debugSchoolAnnouncementItems
import timber.log.Timber
import javax.inject.Inject

class NotificationDebugPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val newGradeNotification: NewGradeNotification,
    private val newHomeworkNotification: NewHomeworkNotification,
    private val newConferenceNotification: NewConferenceNotification,
    private val newExamNotification: NewExamNotification,
    private val newMessageNotification: NewMessageNotification,
    private val newNoteNotification: NewNoteNotification,
    private val newSchoolAnnouncementNotification: NewSchoolAnnouncementNotification,
    private val newLuckyNumberNotification: NewLuckyNumberNotification,
) : BasePresenter<NotificationDebugView>(errorHandler, studentRepository) {

    private val items = listOf(
        NotificationDebugItem(R.string.grade_title) {
            newGradeNotification.notifyDetails(debugGradeDetailsItems.take(it))
        },
        NotificationDebugItem(R.string.grade_summary_predicted_grade) {
            newGradeNotification.notifyPredicted(debugGradeSummaryItems.take(it))
        },
        NotificationDebugItem(R.string.grade_summary_final_grade) {
            newGradeNotification.notifyFinal(debugGradeSummaryItems.take(it))
        },
        NotificationDebugItem(R.string.homework_title) {
            newHomeworkNotification.notify(debugHomeworkItems.take(it))
        },
        NotificationDebugItem(R.string.conferences_title) {
            newConferenceNotification.notify(debugConferenceItems.take(it))
        },
        NotificationDebugItem(R.string.exam_title) {
            newExamNotification.notify(debugExamItems.take(it))
        },
        NotificationDebugItem(R.string.message_title) {
            newMessageNotification.notify(debugMessageItems.take(it))
        },
        NotificationDebugItem(R.string.note_title) {
            newNoteNotification.notify(debugNoteItems.take(it))
        },
        NotificationDebugItem(R.string.school_announcement_title) {
            newSchoolAnnouncementNotification.notify(debugSchoolAnnouncementItems.take(it))
        },
        NotificationDebugItem(R.string.lucky_number_title) {
            repeat(it) {
                newLuckyNumberNotification.notify(debugLuckyNumber)
            }
        },
    )

    override fun onAttachView(view: NotificationDebugView) {
        super.onAttachView(view)
        Timber.i("Notification debug view was initialized")
        with(view) {
            initView()
            setItems(items)
        }
    }
}
