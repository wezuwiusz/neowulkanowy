package io.github.wulkanowy.ui.modules.grade.summary

import io.github.wulkanowy.data.db.entities.GradeSummary
import io.github.wulkanowy.data.repositories.GradeRepository
import io.github.wulkanowy.data.repositories.GradeSummaryRepository
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.session.BaseSessionPresenter
import io.github.wulkanowy.ui.base.session.SessionErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.calcAverage
import io.github.wulkanowy.utils.changeModifier
import java.lang.String.format
import java.util.Locale.FRANCE
import javax.inject.Inject

class GradeSummaryPresenter @Inject constructor(
    private val errorHandler: SessionErrorHandler,
    private val gradeSummaryRepository: GradeSummaryRepository,
    private val gradeRepository: GradeRepository,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val preferencesRepository: PreferencesRepository,
    private val schedulers: SchedulersProvider,
    private val analytics: FirebaseAnalyticsHelper
) : BaseSessionPresenter<GradeSummaryView>(errorHandler) {

    override fun onAttachView(view: GradeSummaryView) {
        super.onAttachView(view)
        view.initView()
    }

    fun onParentViewLoadData(semesterId: Int, forceRefresh: Boolean) {
        disposable.add(studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getSemesters(it) }
            .map { semester -> semester.first { it.semesterId == semesterId } }
            .flatMap {
                gradeSummaryRepository.getGradesSummary(it, forceRefresh)
                    .flatMap { gradesSummary ->
                        gradeRepository.getGrades(it, forceRefresh)
                            .map { grades ->
                                grades.map { item -> item.changeModifier(preferencesRepository.gradePlusModifier, preferencesRepository.gradeMinusModifier) }
                                    .groupBy { grade -> grade.subject }
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
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
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
                    updateData(it.first, it.second)
                }
                analytics.logEvent("load_grade_summary", mapOf("items" to it.first.size, "force_refresh" to forceRefresh))
            }) {
                view?.run { showEmpty(isViewEmpty) }
                errorHandler.dispatch(it)
            })
    }

    fun onSwipeRefresh() {
        view?.notifyParentRefresh()
    }

    fun onParentViewReselected() {
        view?.run {
            if (!isViewEmpty) resetView()
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
                        title = view?.predictedString.orEmpty(),
                        grade = gradeSummary.predictedGrade
                    ), GradeSummaryItem(
                        header = it,
                        title = view?.finalString.orEmpty(),
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
