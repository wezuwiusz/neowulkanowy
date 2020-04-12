package io.github.wulkanowy.ui.modules.schoolandteachers.teacher

import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.data.repositories.teacher.TeacherRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import timber.log.Timber
import javax.inject.Inject

class TeacherPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val teacherRepository: TeacherRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<TeacherView>(errorHandler, studentRepository, schedulers) {

    private lateinit var lastError: Throwable

    override fun onAttachView(view: TeacherView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Teacher view was initialized")
        errorHandler.showErrorMessage = ::showErrorViewOnError
        loadData()
    }

    fun onSwipeRefresh() {
        loadData(true)
    }

    fun onRetry() {
        view?.run {
            showErrorView(false)
            showProgress(true)
        }
        loadData(true)
    }

    fun onDetailsClick() {
        view?.showErrorDetailsDialog(lastError)
    }

    fun onParentViewLoadData(forceRefresh: Boolean) {
        loadData(forceRefresh)
    }

    private fun loadData(forceRefresh: Boolean = false) {
        Timber.i("Loading teachers data started")
        disposable.add(studentRepository.getCurrentStudent()
            .flatMap { student ->
                semesterRepository.getCurrentSemester(student).flatMap { semester ->
                    teacherRepository.getTeachers(student, semester, forceRefresh)
                }
            }
            .map { it.filter { teacher -> teacher.name.isNotBlank() } }
            .map { items -> items.map { TeacherItem(it, view?.noSubjectString.orEmpty()) } }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doFinally {
                view?.run {
                    hideRefresh()
                    showProgress(false)
                    enableSwipe(true)
                    notifyParentDataLoaded()
                }
            }.subscribe({
                Timber.i("Loading teachers result: Success")
                view?.run {
                    updateData(it)
                    showContent(it.isNotEmpty())
                    showEmpty(it.isEmpty())
                    showErrorView(false)
                }
                analytics.logEvent("load_teachers", "items" to it.size, "force_refresh" to forceRefresh)
            }) {
                Timber.i("Loading teachers result: An exception occurred")
                errorHandler.dispatch(it)
            })
    }

    private fun showErrorViewOnError(message: String, error: Throwable) {
        view?.run {
            if (isViewEmpty) {
                lastError = error
                setErrorDetails(message)
                showErrorView(true)
                showEmpty(false)
            } else showError(message, error)
        }
    }
}
