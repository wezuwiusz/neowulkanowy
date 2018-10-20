package io.github.wulkanowy.ui.main.grade.summary

import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.db.entities.GradeSummary
import io.github.wulkanowy.data.repositories.GradeRepository
import io.github.wulkanowy.data.repositories.GradeSummaryRepository
import io.github.wulkanowy.data.repositories.SessionRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.calcAverage
import io.github.wulkanowy.utils.schedulers.SchedulersManager
import java.lang.String.format
import java.util.Locale.FRANCE
import javax.inject.Inject

class GradeSummaryPresenter @Inject constructor(
        private val errorHandler: ErrorHandler,
        private val gradeSummaryRepository: GradeSummaryRepository,
        private val gradeRepository: GradeRepository,
        private val sessionRepository: SessionRepository,
        private val schedulers: SchedulersManager)
    : BasePresenter<GradeSummaryView>(errorHandler) {

    override fun onAttachView(view: GradeSummaryView) {
        super.onAttachView(view)
        view.initView()
    }

    fun onParentViewLoadData(semesterId: Int, forceRefresh: Boolean) {
        disposable.add(sessionRepository.getSemesters()
                .map { semester -> semester.first { it.semesterId == semesterId } }
                .flatMap {
                    gradeSummaryRepository.getGradesSummary(it, forceRefresh)
                            .flatMap { gradesSummary ->
                                gradeRepository.getGrades(it, forceRefresh)
                                        .map { grades ->
                                            grades.groupBy { grade -> grade.subject }
                                                    .mapValues { entry -> entry.value.calcAverage() }
                                                    .filterValues { value -> value != 0.0 }
                                                    .let { averages ->
                                                        createGradeSummaryItems(gradesSummary, averages) to
                                                                GradeSummaryScrollableHeader(
                                                                        formatAverage(gradesSummary.calcAverage()),
                                                                        formatAverage(averages.values.average())
                                                                )
                                                    }
                                        }
                            }
                }
                .subscribeOn(schedulers.backgroundThread())
                .observeOn(schedulers.mainThread())
                .doFinally {
                    view?.run {
                        showRefresh(false)
                        showProgress(false)
                        notifyParentDataLoaded(semesterId)
                    }
                }.subscribe({
                    view?.run {
                        showEmpty(it.first.isEmpty())
                        showContent(it.first.isNotEmpty())
                        updateDataSet(it.first, it.second)
                    }
                }) {
                    view?.run { showEmpty(isViewEmpty()) }
                    errorHandler.proceed(it)
                })
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

    private fun createGradeSummaryItems(gradesSummary: List<GradeSummary>, averages: Map<String, Double>)
            : List<GradeSummaryItem> {
        return gradesSummary.filter { !checkEmpty(it, averages) }
                .flatMap { gradeSummary ->
                    GradeSummaryHeader(
                            name = gradeSummary.subject,
                            average = formatAverage(averages.getOrElse(gradeSummary.subject) { 0.0 }, "")
                    ).let {
                        listOf(GradeSummaryItem(
                                header = it,
                                title = view?.predictedString().orEmpty(),
                                grade = gradeSummary.predictedGrade
                        ), GradeSummaryItem(
                                header = it,
                                title = view?.finalString().orEmpty(),
                                grade = gradeSummary.finalGrade
                        ))
                    }
                }
    }

    private fun checkEmpty(gradeSummary: GradeSummary, averages: Map<String, Double>): Boolean {
        return gradeSummary.run {
            finalGrade.isBlank() && predictedGrade.isBlank() && averages[subject] == null
        }
    }

    private fun formatAverage(average: Double, defaultValue: String = "-- --"): String {
        return if (average == 0.0) defaultValue
        else format(FRANCE, "%.2f", average)
    }
}
