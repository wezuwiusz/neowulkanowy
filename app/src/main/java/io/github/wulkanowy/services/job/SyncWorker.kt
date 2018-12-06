package io.github.wulkanowy.services.job

import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.SimpleJobService
import dagger.android.AndroidInjection
import io.github.wulkanowy.data.repositories.AttendanceRepository
import io.github.wulkanowy.data.repositories.ExamRepository
import io.github.wulkanowy.data.repositories.GradeRepository
import io.github.wulkanowy.data.repositories.GradeSummaryRepository
import io.github.wulkanowy.data.repositories.HomeworkRepository
import io.github.wulkanowy.data.repositories.MessagesRepository
import io.github.wulkanowy.data.repositories.MessagesRepository.MessageFolder.RECEIVED
import io.github.wulkanowy.data.repositories.NoteRepository
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.repositories.TimetableRepository
import io.github.wulkanowy.services.notification.GradeNotification
import io.github.wulkanowy.services.notification.MessageNotification
import io.github.wulkanowy.services.notification.NoteNotification
import io.github.wulkanowy.utils.friday
import io.github.wulkanowy.utils.isHolidays
import io.github.wulkanowy.utils.monday
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import org.threeten.bp.LocalDate
import timber.log.Timber
import javax.inject.Inject

class SyncWorker : SimpleJobService() {

    @Inject
    lateinit var student: StudentRepository

    @Inject
    lateinit var semester: SemesterRepository

    @Inject
    lateinit var gradesDetails: GradeRepository

    @Inject
    lateinit var gradesSummary: GradeSummaryRepository

    @Inject
    lateinit var attendance: AttendanceRepository

    @Inject
    lateinit var exam: ExamRepository

    @Inject
    lateinit var timetable: TimetableRepository

    @Inject
    lateinit var message: MessagesRepository

    @Inject
    lateinit var note: NoteRepository

    @Inject
    lateinit var homework: HomeworkRepository

    @Inject
    lateinit var prefRepository: PreferencesRepository

    private val disposable = CompositeDisposable()

    companion object {
        const val WORK_TAG = "FULL_SYNC"
    }

    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)
    }

    override fun onRunJob(job: JobParameters?): Int {
        Timber.d("Synchronization started")

        val start = LocalDate.now().monday
        val end = LocalDate.now().friday

        if (start.isHolidays) return RESULT_FAIL_NORETRY

        var error: Throwable? = null

        disposable.add(student.getCurrentStudent()
            .flatMap { semester.getCurrentSemester(it, true) }
            .flatMapPublisher {
                Single.merge(
                    listOf(
                        gradesDetails.getGrades(it, true, true),
                        gradesSummary.getGradesSummary(it, true),
                        attendance.getAttendance(it, start, end, true),
                        exam.getExams(it, start, end, true),
                        timetable.getTimetable(it, start, end, true),
                        message.getMessages(it.studentId, RECEIVED, true, true),
                        note.getNotes(it, true, true),
                        homework.getHomework(it, LocalDate.now(), true),
                        homework.getHomework(it, LocalDate.now().plusDays(1), true)
                    )
                )
            }
            .subscribe({}, { error = it }))

        return if (null === error) {
            if (prefRepository.isNotificationsEnable) sendNotifications()
            Timber.d("Synchronization successful")
            RESULT_SUCCESS
        } else {
            Timber.e(error, "Synchronization failed")
            RESULT_FAIL_RETRY
        }
    }

    private fun sendNotifications() {
        sendGradeNotifications()
        sendMessageNotification()
        sendNoteNotification()
    }

    private fun sendGradeNotifications() {
        disposable.add(student.getCurrentStudent()
            .flatMap { semester.getCurrentSemester(it) }
            .flatMap { gradesDetails.getNewGrades(it) }
            .map { it.filter { grade -> !grade.isNotified } }
            .doOnSuccess {
                if (it.isNotEmpty()) {
                    Timber.d("Found ${it.size} unread grades")
                    GradeNotification(applicationContext).sendNotification(it)
                }
            }
            .map { it.map { grade -> grade.apply { isNotified = true } } }
            .flatMapCompletable { gradesDetails.updateGrades(it) }
            .subscribe({}, { Timber.e(it, "Grade notifications sending failed") }))
    }

    private fun sendMessageNotification() {
        disposable.add(student.getCurrentStudent()
            .flatMap { message.getNewMessages(it) }
            .map { it.filter { message -> !message.isNotified } }
            .doOnSuccess{
                if (it.isNotEmpty()) {
                    Timber.d("Found ${it.size} unread messages")
                    MessageNotification(applicationContext).sendNotification(it)
                }
            }
            .map { it.map { message -> message.apply { isNotified = true } } }
            .flatMapCompletable { message.updateMessages(it) }
            .subscribe({}, { Timber.e(it, "Message notifications sending failed") })
        )
    }

    private fun sendNoteNotification() {
        disposable.add(student.getCurrentStudent()
            .flatMap { semester.getCurrentSemester(it) }
            .flatMap { note.getNewNotes(it) }
            .map { it.filter { note -> !note.isNotified } }
            .doOnSuccess {
                if (it.isNotEmpty()) {
                    Timber.d("Found ${it.size} unread notes")
                    NoteNotification(applicationContext).sendNotification(it)
                }
            }
            .map { it.map { note -> note.apply { isNotified = true } } }
            .flatMapCompletable { note.updateNotes(it) }
            .subscribe({}, { Timber.e("Notifications sending failed") })
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}
