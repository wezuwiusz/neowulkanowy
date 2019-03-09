package io.github.wulkanowy.ui.modules.grade.statistics

import io.github.wulkanowy.data.db.entities.Subject
import io.github.wulkanowy.data.repositories.gradestatistics.GradeStatisticsRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.data.repositories.subject.SubjectRepository
import io.github.wulkanowy.ui.base.session.BaseSessionPresenter
import io.github.wulkanowy.ui.base.session.SessionErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import timber.log.Timber
import javax.inject.Inject

class GradeStatisticsPresenter @Inject constructor(
    private val errorHandler: SessionErrorHandler,
    private val gradeStatisticsRepository: GradeStatisticsRepository,
    private val subjectRepository: SubjectRepository,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val schedulers: SchedulersProvider,
    private val analytics: FirebaseAnalyticsHelper
) : BaseSessionPresenter<GradeStatisticsView>(errorHandler) {

    private var subjects = emptyList<Subject>()

    private var currentSemesterId = 0

    private var currentSubjectName: String = "Wszystkie"

    var currentIsSemester = false
        private set

    fun onAttachView(view: GradeStatisticsView, isSemester: Boolean?) {
        super.onAttachView(view)
        currentIsSemester = isSemester ?: false
        view.initView()
    }

    fun onParentViewLoadData(semesterId: Int, forceRefresh: Boolean) {
        currentSemesterId = semesterId
        loadSubjects()
        loadData(semesterId, currentSubjectName, currentIsSemester, forceRefresh)
    }

    fun onParentViewChangeSemester() {
        view?.run {
            showProgress(true)
            enableSwipe(false)
            showRefresh(false)
            showContent(false)
            showEmpty(false)
            clearView()
        }
        disposable.clear()
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the grade stats")
        view?.notifyParentRefresh()
    }

    fun onSubjectSelected(name: String) {
        Timber.i("Select attendance stats subject $name")
        view?.run {
            showContent(false)
            showProgress(true)
            enableSwipe(false)
            showEmpty(false)
            clearView()
        }
        (subjects.singleOrNull { it.name == name }?.name).let {
            if (it != currentSubjectName) loadData(currentSemesterId, name, currentIsSemester)
        }
    }

    fun onTypeChange(isSemester: Boolean) {
        Timber.i("Select attendance stats semester: $isSemester")
        disposable.clear()
        view?.run {
            showContent(false)
            showProgress(true)
            enableSwipe(false)
            showEmpty(false)
            clearView()
        }
        loadData(currentSemesterId, currentSubjectName, isSemester)
    }

    private fun loadSubjects() {
        Timber.i("Loading grade stats subjects started")
        disposable.add(studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getCurrentSemester(it) }
            .flatMap { subjectRepository.getSubjects(it) }
            .doOnSuccess { subjects = it }
            .map { ArrayList(it.map { subject -> subject.name }) }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({
                Timber.i("Loading grade stats subjects result: Success")
                view?.run {
                    updateSubjects(it)
                    showSubjects(true)
                }
            }, {
                Timber.e("Loading grade stats subjects result: An exception occurred")
                errorHandler.dispatch(it)
            })
        )
    }

    private fun loadData(semesterId: Int, subjectName: String, isSemester: Boolean, forceRefresh: Boolean = false) {
        Timber.i("Loading grade stats data started")
        currentSubjectName = subjectName
        currentIsSemester = isSemester
        disposable.add(studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getSemesters(it) }
            .flatMap { gradeStatisticsRepository.getGradesStatistics(it.first { item -> item.semesterId == semesterId }, subjectName, isSemester, forceRefresh) }
            .map { list -> list.sortedByDescending { it.grade } }
            .map { list -> list.filter { it.amount != 0 } }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doFinally {
                view?.run {
                    showRefresh(false)
                    showProgress(false)
                    enableSwipe(true)
                    notifyParentDataLoaded(semesterId)
                }
            }
            .subscribe({
                Timber.i("Loading grade stats result: Success")
                view?.run {
                    showEmpty(it.isEmpty())
                    showContent(it.isNotEmpty())
                    updateData(it)
                }
                analytics.logEvent("load_grade_statistics", "items" to it.size, "force_refresh" to forceRefresh)
            }) {
                Timber.e("Loading grade stats result: An exception occurred")
                view?.run { showEmpty(isViewEmpty) }
                errorHandler.dispatch(it)
            })
    }
}
