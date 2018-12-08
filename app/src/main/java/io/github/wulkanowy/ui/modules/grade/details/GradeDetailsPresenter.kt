package io.github.wulkanowy.ui.modules.grade.details

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.repositories.GradeRepository
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.modules.main.MainErrorHandler
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.calcAverage
import io.github.wulkanowy.utils.changeModifier
import io.github.wulkanowy.utils.logEvent
import io.github.wulkanowy.utils.valueColor
import timber.log.Timber
import javax.inject.Inject

class GradeDetailsPresenter @Inject constructor(
    private val errorHandler: MainErrorHandler,
    private val schedulers: SchedulersProvider,
    private val gradeRepository: GradeRepository,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val preferencesRepository: PreferencesRepository
) : BasePresenter<GradeDetailsView>(errorHandler) {

    private var currentSemesterId = 0

    override fun onAttachView(view: GradeDetailsView) {
        super.onAttachView(view)
        view.initView()
    }

    fun onParentViewLoadData(semesterId: Int, forceRefresh: Boolean) {
        currentSemesterId = semesterId
        loadData(semesterId, forceRefresh)
    }

    fun onGradeItemSelected(item: AbstractFlexibleItem<*>?) {
        if (item is GradeDetailsItem) {
            view?.apply {
                showGradeDialog(item.grade)
                if (!item.grade.isRead) {
                    item.grade.isRead = true
                    updateItem(item)
                    getHeaderOfItem(item)?.let { header ->
                        if (header is GradeDetailsHeader) {
                            header.newGrades--
                            updateItem(header)
                        }
                    }
                    updateGrade(item.grade)
                }
            }
        }
    }

    fun onMarkAsReadSelected(): Boolean {
        disposable.add(studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getSemesters(it) }
            .flatMap { gradeRepository.getNewGrades(it.first { item -> item.semesterId == currentSemesterId }) }
            .map { it.map { grade -> grade.apply { isRead = true } } }
            .flatMapCompletable { gradeRepository.updateGrades(it) }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({ loadData(currentSemesterId, false) }, { errorHandler.dispatch(it) }))
        return true
    }

    fun onSwipeRefresh() {
        view?.notifyParentRefresh()
    }

    fun onParentViewReselected() {
        view?.run {
            if (!isViewEmpty) {
                if (preferencesRepository.isGradeExpandable) collapseAllItems()
                scrollToStart()
            }
        }
    }

    fun onParentViewChangeSemester() {
        view?.run {
            showProgress(true)
            showRefresh(false)
            showContent(false)
            showEmpty(false)
            clearView()
        }
        disposable.clear()
    }

    private fun loadData(semesterId: Int, forceRefresh: Boolean) {
        disposable.add(studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getSemesters(it) }
            .flatMap { gradeRepository.getGrades(it.first { item -> item.semesterId == semesterId }, forceRefresh) }
            .map { it.map { item -> item.changeModifier(preferencesRepository.gradePlusModifier, preferencesRepository.gradeMinusModifier) } }
            .map { createGradeItems(it.groupBy { grade -> grade.subject }.toSortedMap()) }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doFinally {
                view?.run {
                    showRefresh(false)
                    showProgress(false)
                    notifyParentDataLoaded(semesterId)
                }
            }
            .subscribe({
                view?.run {
                    showEmpty(it.isEmpty())
                    showContent(it.isNotEmpty())
                    updateData(it)
                }
                logEvent("Grade details load", mapOf("items" to it.size, "forceRefresh" to forceRefresh))
            }) {
                view?.run { showEmpty(isViewEmpty) }
                errorHandler.dispatch(it)
            })
    }

    private fun createGradeItems(items: Map<String, List<Grade>>): List<GradeDetailsHeader> {
        return items.map {
            it.value.calcAverage().let { average ->
                GradeDetailsHeader(
                    subject = it.key,
                    average = formatAverage(average),
                    number = view?.getGradeNumberString(it.value.size).orEmpty(),
                    newGrades = it.value.filter { grade -> !grade.isRead }.size,
                    isExpandable = preferencesRepository.isGradeExpandable
                ).apply {
                    subItems = it.value.map { item ->
                        GradeDetailsItem(
                            grade = item,
                            weightString = view?.weightString.orEmpty(),
                            valueColor = item.valueColor
                        )
                    }
                }
            }
        }
    }

    private fun formatAverage(average: Double): String {
        return view?.run {
            if (average == 0.0) emptyAverageString
            else averageString.format(average)
        }.orEmpty()
    }

    private fun updateGrade(grade: Grade) {
        disposable.add(gradeRepository.updateGrade(grade)
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({}) { error -> errorHandler.dispatch(error) })
        Timber.d("Grade ${grade.id} updated")
    }
}
