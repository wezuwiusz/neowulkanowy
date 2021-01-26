package io.github.wulkanowy.ui.modules.grade

import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.flowWithResource
import io.github.wulkanowy.utils.getCurrentOrLast
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class GradePresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val analytics: AnalyticsHelper
) : BasePresenter<GradeView>(errorHandler, studentRepository) {

    private var selectedIndex = 0

    private var schoolYear = 0

    private var semesters = emptyList<Semester>()

    private val loadedSemesterId = mutableMapOf<Int, Int>()

    private lateinit var lastError: Throwable

    override fun onAttachView(view: GradeView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Grade view was initialized with $selectedIndex index")
        errorHandler.showErrorMessage = ::showErrorViewOnError
        loadData()
    }

    fun onCreateMenu() {
        if (semesters.isEmpty()) view?.showSemesterSwitch(false)
    }

    fun onViewReselected() {
        Timber.i("Grade view is reselected")
        view?.run { notifyChildParentReselected(currentPageIndex) }
    }

    fun onSemesterSwitch(): Boolean {
        if (semesters.isNotEmpty()) view?.showSemesterDialog(selectedIndex - 1)
        return true
    }

    fun onSemesterSelected(index: Int) {
        if (selectedIndex != index - 1) {
            Timber.i("Change semester in grade view to ${index + 1}")
            selectedIndex = index + 1
            loadedSemesterId.clear()
            view?.let {
                it.setCurrentSemesterName(index + 1, schoolYear)
                notifyChildrenSemesterChange()
                loadChild(it.currentPageIndex)
            }
            analytics.logEvent("changed_semester", "number" to index + 1)
        }
    }

    fun onChildViewRefresh() {
        view?.let { loadChild(it.currentPageIndex, true) }
    }

    fun onChildViewLoaded(semesterId: Int) {
        view?.apply {
            showContent(true)
            showProgress(false)
            showErrorView(false)
            loadedSemesterId[currentPageIndex] = semesterId
        }
    }

    fun onPageSelected(index: Int) {
        if (semesters.isNotEmpty()) loadChild(index)
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

    private fun loadData() {
        flowWithResource {
            val student = studentRepository.getCurrentStudent()
            delay(200)
            semesterRepository.getSemesters(student, refreshOnNoCurrent = true)
        }.onEach {
            when (it.status) {
                Status.LOADING -> Timber.i("Loading grade data started")
                Status.SUCCESS -> {
                    val current = it.data!!.getCurrentOrLast()
                    selectedIndex = if (selectedIndex == 0) current.semesterName else selectedIndex
                    schoolYear = current.schoolYear
                    semesters = it.data.filter { semester -> semester.diaryId == current.diaryId }
                    view?.setCurrentSemesterName(current.semesterName, schoolYear)

                    view?.run {
                        Timber.i("Loading grade result: Attempt load index $currentPageIndex")
                        loadChild(currentPageIndex)
                        showErrorView(false)
                        showSemesterSwitch(true)
                    }
                }
                Status.ERROR -> {
                    Timber.i("Loading grade result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                }
            }
        }.launch()
    }

    private fun showErrorViewOnError(message: String, error: Throwable) {
        lastError = error
        view?.run {
            showProgress(false)
            showErrorView(true)
            setErrorDetails(message)
        }
    }

    private fun loadChild(index: Int, forceRefresh: Boolean = false) {
        Timber.d("Load grade tab child. Selected semester: $selectedIndex, semesters: ${semesters.joinToString { it.semesterName.toString() }}")
        semesters.first { it.semesterName == selectedIndex }.semesterId.also {
            if (forceRefresh || loadedSemesterId[index] != it) {
                Timber.i("Load grade child view index: $index")
                view?.notifyChildLoadData(index, it, forceRefresh)
            }
        }
    }

    private fun notifyChildrenSemesterChange() {
        for (i in 0..2) view?.notifyChildSemesterChange(i)
    }
}
