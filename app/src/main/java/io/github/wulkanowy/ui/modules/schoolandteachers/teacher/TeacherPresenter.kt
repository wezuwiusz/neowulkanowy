package io.github.wulkanowy.ui.modules.schoolandteachers.teacher

import io.github.wulkanowy.data.*
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.repositories.TeacherRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import kotlinx.coroutines.flow.catch
import timber.log.Timber
import javax.inject.Inject

class TeacherPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val teacherRepository: TeacherRepository,
    private val analytics: AnalyticsHelper
) : BasePresenter<TeacherView>(errorHandler, studentRepository) {

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
        loadData()
    }

    fun onDetailsClick() {
        view?.showErrorDetailsDialog(lastError)
    }

    fun onParentViewLoadData(forceRefresh: Boolean) {
        loadData(forceRefresh)
    }

    private fun loadData(forceRefresh: Boolean = false) {
        flatResourceFlow {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            teacherRepository.getTeachers(student, semester, forceRefresh)
        }
            .logResourceStatus("load teachers data")
            .onResourceData {
                view?.run {
                    updateData(it
                        .filter { item -> item.name.isNotBlank() }
                        .sortedBy { it.name }
                    )
                    showContent(it.isNotEmpty())
                    showEmpty(it.isEmpty())
                    showErrorView(false)
                }
            }
            .onResourceSuccess {
                analytics.logEvent(
                    "load_data",
                    "type" to "teachers",
                    "items" to it.size
                )
            }
            .onResourceNotLoading {
                view?.run {
                    hideRefresh()
                    showProgress(false)
                    enableSwipe(true)
                    notifyParentDataLoaded()
                }
            }
            .onResourceError(errorHandler::dispatch)
            .catch {
                errorHandler.dispatch(it)
                view?.notifyParentDataLoaded()
            }
            .launch()
    }

    private fun showErrorViewOnError(message: String, error: Throwable) {
        view?.run {
            if (isViewEmpty) {
                lastError = error
                setErrorDetails(message)
                showErrorView(true)
                showEmpty(false)
                showProgress(false)
            } else showError(message, error)
        }
    }
}
