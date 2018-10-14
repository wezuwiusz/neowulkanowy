package io.github.wulkanowy.ui.main.grade.details

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.repositories.GradeRepository
import io.github.wulkanowy.data.repositories.SessionRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.calcAverage
import io.github.wulkanowy.utils.schedulers.SchedulersManager
import io.github.wulkanowy.utils.valueColor
import javax.inject.Inject

class GradeDetailsPresenter @Inject constructor(
        private val errorHandler: ErrorHandler,
        private val schedulers: SchedulersManager,
        private val gradeRepository: GradeRepository,
        private val sessionRepository: SessionRepository) : BasePresenter<GradeDetailsView>(errorHandler) {

    override fun onAttachView(view: GradeDetailsView) {
        super.onAttachView(view)
        view.initView()
    }

    fun onParentViewLoadData(semesterId: String, forceRefresh: Boolean) {
        disposable.add(sessionRepository.getSemesters()
                .flatMap { gradeRepository.getGrades(it.first { item -> item.semesterId == semesterId }, forceRefresh) }
                .map { createGradeItems(it.groupBy { grade -> grade.subject }.toSortedMap()) }
                .subscribeOn(schedulers.backgroundThread())
                .observeOn(schedulers.mainThread())
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
                }) {
                    view?.run { showEmpty(isViewEmpty()) }
                    errorHandler.proceed(it)
                })
    }

    fun onGradeItemSelected(item: AbstractFlexibleItem<*>?) {
        if (item is GradeDetailsItem) {
            view?.apply {
                showGradeDialog(item.grade)
                if (item.grade.isNew) {
                    item.grade.isNew = false
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

    fun onSwipeRefresh() {
        view?.notifyParentRefresh()
    }

    fun onParentViewReselected() {
        view?.run {
            if (!isViewEmpty()) resetView()
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

    private fun createGradeItems(items: Map<String, List<Grade>>): List<GradeDetailsHeader> {
        return items.map {
            it.value.calcAverage().let { average ->
                GradeDetailsHeader(
                        subject = it.key,
                        average = formatAverage(average),
                        number = view?.gradeNumberString(it.value.size).orEmpty(),
                        newGrades = it.value.filter { grade -> grade.isNew }.size
                ).apply {
                    subItems = it.value.map { item ->
                        GradeDetailsItem(
                                grade = item,
                                weightString = view?.weightString().orEmpty(),
                                valueColor = item.valueColor
                        )
                    }
                }
            }
        }
    }

    private fun formatAverage(average: Double): String {
        return view?.run {
            if (average == 0.0) emptyAverageString()
            else averageString().format(average)
        }.orEmpty()
    }

    private fun updateGrade(grade: Grade) {
        disposable.add(gradeRepository.updateGrade(grade)
                .subscribeOn(schedulers.backgroundThread())
                .observeOn(schedulers.mainThread())
                .subscribe({}) { error -> errorHandler.proceed(error) })
    }
}
