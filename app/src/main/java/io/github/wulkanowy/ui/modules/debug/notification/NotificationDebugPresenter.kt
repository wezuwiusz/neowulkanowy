package io.github.wulkanowy.ui.modules.debug.notification

import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.services.sync.notifications.ChangeTimetableNotification
import io.github.wulkanowy.services.sync.notifications.NewAttendanceNotification
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
import io.github.wulkanowy.ui.modules.debug.notification.mock.debugAttendanceItems
import io.github.wulkanowy.ui.modules.debug.notification.mock.debugConferenceItems
import io.github.wulkanowy.ui.modules.debug.notification.mock.debugExamItems
import io.github.wulkanowy.ui.modules.debug.notification.mock.debugGradeDetailsItems
import io.github.wulkanowy.ui.modules.debug.notification.mock.debugGradeSummaryItems
import io.github.wulkanowy.ui.modules.debug.notification.mock.debugHomeworkItems
import io.github.wulkanowy.ui.modules.debug.notification.mock.debugLuckyNumber
import io.github.wulkanowy.ui.modules.debug.notification.mock.debugMessageItems
import io.github.wulkanowy.ui.modules.debug.notification.mock.debugNoteItems
import io.github.wulkanowy.ui.modules.debug.notification.mock.debugSchoolAnnouncementItems
import io.github.wulkanowy.ui.modules.debug.notification.mock.debugTimetableItems
import kotlinx.coroutines.launch
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
    private val changeTimetableNotification: ChangeTimetableNotification,
    private val newAttendanceNotification: NewAttendanceNotification,
) : BasePresenter<NotificationDebugView>(errorHandler, studentRepository) {

    private val items = listOf(
        NotificationDebugItem(R.string.grade_title) { n ->
            withStudent { newGradeNotification.notifyDetails(debugGradeDetailsItems.take(n), it) }
        },
        NotificationDebugItem(R.string.grade_summary_predicted_grade) { n ->
            withStudent { newGradeNotification.notifyPredicted(debugGradeSummaryItems.take(n), it) }
        },
        NotificationDebugItem(R.string.grade_summary_final_grade) { n ->
            withStudent { newGradeNotification.notifyFinal(debugGradeSummaryItems.take(n), it) }
        },
        NotificationDebugItem(R.string.homework_title) { n ->
            withStudent { newHomeworkNotification.notify(debugHomeworkItems.take(n), it) }
        },
        NotificationDebugItem(R.string.conferences_title) { n ->
            withStudent { newConferenceNotification.notify(debugConferenceItems.take(n), it) }
        },
        NotificationDebugItem(R.string.exam_title) { n ->
            withStudent { newExamNotification.notify(debugExamItems.take(n), it) }
        },
        NotificationDebugItem(R.string.message_title) { n ->
            withStudent { newMessageNotification.notify(debugMessageItems.take(n), it) }
        },
        NotificationDebugItem(R.string.note_title) { n ->
            withStudent { newNoteNotification.notify(debugNoteItems.take(n), it) }
        },
        NotificationDebugItem(R.string.attendance_title) { n ->
            withStudent { newAttendanceNotification.notify(debugAttendanceItems.take(n), it) }
        },
        NotificationDebugItem(R.string.timetable_title) { n ->
            withStudent { changeTimetableNotification.notify(debugTimetableItems.take(n), it) }
        },
        NotificationDebugItem(R.string.school_announcement_title) { n ->
            withStudent {
                newSchoolAnnouncementNotification.notify(debugSchoolAnnouncementItems.take(n), it)
            }
        },
        NotificationDebugItem(R.string.lucky_number_title) { n ->
            withStudent {
                repeat(n) { _ ->
                    newLuckyNumberNotification.notify(debugLuckyNumber, it)
                }
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

    private fun withStudent(block: suspend (Student) -> Unit) {
        presenterScope.launch {
            block(studentRepository.getCurrentStudent(false))
        }
    }
}
