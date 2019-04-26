package io.github.wulkanowy.ui.modules.grade

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.session.BaseSessionPresenter
import io.github.wulkanowy.ui.base.session.SessionErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import timber.log.Timber
import javax.inject.Inject

class GradePresenter @Inject constructor(
    private val errorHandler: SessionErrorHandler,
    private val schedulers: SchedulersProvider,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BaseSessionPresenter<GradeView>(errorHandler) {

    var selectedIndex = 0
        private set

    private var semesters = emptyList<Semester>()

    private val loadedSemesterId = mutableMapOf<Int, Int>()

    fun onAttachView(view: GradeView, savedIndex: Int?) {
        super.onAttachView(view)
        selectedIndex = savedIndex ?: 0
        view.run {
            initView()
            enableSwipe(false)
        }
        Timber.i("Grade view was initialized with $selectedIndex index")
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
            showEmpty(false)
            loadedSemesterId[currentPageIndex] = semesterId
        }
    }

    fun onPageSelected(index: Int) {
        if (semesters.isNotEmpty()) loadChild(index)
    }

    fun onSwipeRefresh() {
        loadData()
    }

    private fun loadData() {
        Timber.i("Loading grade data started")
        disposable.add(studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getSemesters(it) }
            .doOnSuccess {
                it.first { item -> item.isCurrent }.also { current ->
                    selectedIndex = if (selectedIndex == 0) current.semesterName else selectedIndex
                    semesters = it.filter { semester -> semester.diaryId == current.diaryId }
                }
            }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doFinally { view?.showRefresh(false) }
            .subscribe({
                view?.run {
                    Timber.i("Loading grade result: Attempt load index $currentPageIndex")
                    loadChild(currentPageIndex)
                    enableSwipe(false)
                    showSemesterSwitch(true)
                }
            }) {
                Timber.i("Loading grade result: An exception occurred")
                errorHandler.dispatch(it)
                view?.run {
                    showProgress(false)
                    showEmpty(true)
                    enableSwipe(true)
                }
            })
    }

    private fun loadChild(index: Int, forceRefresh: Boolean = false) {
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
