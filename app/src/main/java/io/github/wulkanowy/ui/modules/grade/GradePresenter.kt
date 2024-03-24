package io.github.wulkanowy.ui.modules.grade

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.logResourceStatus
import io.github.wulkanowy.data.onResourceData
import io.github.wulkanowy.data.onResourceError
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.resourceFlow
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.getCurrentOrLast
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
    private var availableSemesters = emptyList<Semester>()
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
        if (availableSemesters.isEmpty()) view?.showSemesterSwitch(false)
    }

    fun onViewReselected() {
        Timber.i("Grade view is reselected")
        view?.run { notifyChildParentReselected(currentPageIndex) }
    }

    fun onSemesterSwitch(): Boolean {
        if (availableSemesters.isNotEmpty()) {
            view?.showSemesterDialog(selectedIndex - 1, availableSemesters.take(2))
        }
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
        if (availableSemesters.isNotEmpty()) loadChild(index)
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
        resourceFlow {
            val student = studentRepository.getCurrentStudent()
            val semesters = semesterRepository.getSemesters(student, refreshOnNoCurrent = true)

            student to semesters
        }
            .logResourceStatus("load grade data")
            .onResourceData { (student, semesters) ->
                val currentSemester = semesters.getCurrentOrLast()
                selectedIndex =
                    if (selectedIndex == 0) currentSemester.semesterName else selectedIndex
                schoolYear = currentSemester.schoolYear
                availableSemesters = semesters.filter { semester ->
                    semester.diaryId == currentSemester.diaryId
                }

                view?.run {
                    initTabs(if (student.isEduOne == true) 2 else 3)
                    setCurrentSemesterName(currentSemester.semesterName, schoolYear)

                    Timber.i("Loading grade data: Attempt load index $currentPageIndex")
                    loadChild(currentPageIndex)
                    showErrorView(false)
                    showSemesterSwitch(true)
                }
            }
            .onResourceError(errorHandler::dispatch)
            .launch()
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
        Timber.d("Load grade tab child. Selected semester: $selectedIndex, semesters: ${availableSemesters.joinToString { it.semesterName.toString() }}")

        val newSelectedSemesterId = try {
            availableSemesters.first { it.semesterName == selectedIndex }.semesterId
        } catch (e: NoSuchElementException) {
            Timber.e(e, "Selected semester no exists")
            return
        }

        if (forceRefresh || loadedSemesterId[index] != newSelectedSemesterId) {
            Timber.i("Load grade child view index: $index")
            view?.notifyChildLoadData(index, newSelectedSemesterId, forceRefresh)
        }
    }

    private fun notifyChildrenSemesterChange() {
        for (i in 0..2) view?.notifyChildSemesterChange(i)
    }
}
