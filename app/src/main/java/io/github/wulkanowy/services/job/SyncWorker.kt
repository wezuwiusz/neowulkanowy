package io.github.wulkanowy.services.job

import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.SimpleJobService
import dagger.android.AndroidInjection
import io.github.wulkanowy.data.repositories.attendance.AttendanceRepository
import io.github.wulkanowy.data.repositories.completedlessons.CompletedLessonsRepository
import io.github.wulkanowy.data.repositories.exam.ExamRepository
import io.github.wulkanowy.data.repositories.grade.GradeRepository
import io.github.wulkanowy.data.repositories.gradessummary.GradeSummaryRepository
import io.github.wulkanowy.data.repositories.homework.HomeworkRepository
import io.github.wulkanowy.data.repositories.luckynumber.LuckyNumberRepository
import io.github.wulkanowy.data.repositories.message.MessageRepository
import io.github.wulkanowy.data.repositories.message.MessageRepository.MessageFolder.RECEIVED
import io.github.wulkanowy.data.repositories.note.NoteRepository
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.data.repositories.recipient.RecipientRepository
import io.github.wulkanowy.data.repositories.reportingunit.ReportingUnitRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.data.repositories.timetable.TimetableRepository
import io.github.wulkanowy.services.notification.GradeNotification
import io.github.wulkanowy.services.notification.LuckyNumberNotification
import io.github.wulkanowy.services.notification.MessageNotification
import io.github.wulkanowy.services.notification.NoteNotification
import io.github.wulkanowy.utils.friday
import io.github.wulkanowy.utils.isHolidays
import io.github.wulkanowy.utils.monday
import io.reactivex.Completable
import io.reactivex.Maybe
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
    lateinit var message: MessageRepository

    @Inject
    lateinit var note: NoteRepository

    @Inject
    lateinit var homework: HomeworkRepository

    @Inject
    lateinit var luckyNumber: LuckyNumberRepository

    @Inject
    lateinit var completedLessons: CompletedLessonsRepository

    @Inject
    lateinit var reportingUnitRepository: ReportingUnitRepository

    @Inject
    lateinit var recipientRepository: RecipientRepository

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

        val notify = prefRepository.isNotificationsEnable

        disposable.add(student.isStudentSaved()
            .flatMapMaybe { if (it) student.getCurrentStudent().toMaybe() else Maybe.empty() }
            .flatMap { semester.getCurrentSemester(it, true).map { semester -> semester to it }.toMaybe() }
            .flatMapCompletable { c ->
                Completable.merge(
                    listOf(
                        gradesDetails.getGrades(c.second, c.first, true, notify).ignoreElement(),
                        gradesSummary.getGradesSummary(c.first, true).ignoreElement(),
                        attendance.getAttendance(c.first, start, end, true).ignoreElement(),
                        exam.getExams(c.first, start, end, true).ignoreElement(),
                        timetable.getTimetable(c.first, start, end, true).ignoreElement(),
                        message.getMessages(c.second, RECEIVED, true, notify).ignoreElement(),
                        note.getNotes(c.second, c.first, true, notify).ignoreElement(),
                        homework.getHomework(c.first, LocalDate.now(), true).ignoreElement(),
                        homework.getHomework(c.first, LocalDate.now().plusDays(1), true).ignoreElement(),
                        luckyNumber.getLuckyNumber(c.first, true, notify).ignoreElement(),
                        completedLessons.getCompletedLessons(c.first, start, end, true).ignoreElement()
                    ) + reportingUnitRepository.getReportingUnits(c.second, true)
                        .flatMapPublisher { reportingUnits ->
                            Single.merge(reportingUnits.map { recipientRepository.getRecipients(c.second, 2, it, true) })
                        }.ignoreElements()
                )
            }
            .subscribe({}, { error = it }))

        return if (null === error) {
            if (notify) sendNotifications()
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
        sendLuckyNumberNotification()
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
            .doOnSuccess {
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

    private fun sendLuckyNumberNotification() {
        disposable.add(student.getCurrentStudent()
            .flatMap { semester.getCurrentSemester(it) }
            .flatMapMaybe { luckyNumber.getLuckyNumber(it) }
            .filter { !it.isNotified }
            .doOnSuccess {
                LuckyNumberNotification(applicationContext).sendNotification(it)
            }
            .map { it.apply { isNotified = true } }
            .flatMapCompletable { luckyNumber.updateLuckyNumber(it) }
            .subscribe({}, { Timber.e("Lucky number notification sending failed") }))
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}
