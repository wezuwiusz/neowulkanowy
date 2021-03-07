package io.github.wulkanowy.ui.modules.luckynumber.history

import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.repositories.LuckyNumberRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.afterLoading
import io.github.wulkanowy.utils.flowWithResource
import io.github.wulkanowy.utils.isHolidays
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.previousOrSameSchoolDay
import io.github.wulkanowy.utils.sunday
import io.github.wulkanowy.utils.toFormattedString
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

class LuckyNumberHistoryPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val luckyNumberRepository: LuckyNumberRepository,
    private val analytics: AnalyticsHelper
) : BasePresenter<LuckyNumberHistoryView>(errorHandler, studentRepository) {

    private lateinit var lastError: Throwable

    var currentDate: LocalDate = LocalDate.now().previousOrSameSchoolDay

    override fun onAttachView(view: LuckyNumberHistoryView) {
        super.onAttachView(view)
        view.run {
            initView()
            reloadNavigation()
            showContent(false)
        }
        Timber.i("Lucky number history view was initialized")
        errorHandler.showErrorMessage = ::showErrorViewOnError
        loadData()
    }

    private fun loadData() {
        flowWithResource {
            val student = studentRepository.getCurrentStudent()
            luckyNumberRepository.getLuckyNumberHistory(student, currentDate.monday, currentDate.sunday)
        }.onEach {
            when (it.status) {
                Status.LOADING -> Timber.i("Loading lucky number history started")
                Status.SUCCESS -> {
                    if (!it.data?.first().isNullOrEmpty()) {
                        Timber.i("Loading lucky number result: Success")
                        view?.apply {
                            updateData(it.data!!.first())
                            showContent(true)
                            showEmpty(false)
                            showErrorView(false)
                            showProgress(false)
                        }
                        analytics.logEvent(
                            "load_items",
                            "type" to "lucky_number_history",
                            "numbers" to it.data
                        )
                    } else {
                        Timber.i("Loading lucky number history result: No lucky numbers found")
                        view?.run {
                            showContent(false)
                            showEmpty(true)
                            showErrorView(false)
                        }
                    }
                }
                Status.ERROR -> {
                    Timber.i("Loading lucky number history result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                }
            }
        }.afterLoading {
            view?.run {
                showProgress(false)
            }
        }.launch()
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

    private fun reloadView(date: LocalDate) {
        currentDate = date
        Timber.i("Reload lucky number history view with the date ${currentDate.toFormattedString()}")
        view?.apply {
            showProgress(true)
            showContent(false)
            showEmpty(false)
            showErrorView(false)
            clearData()
            reloadNavigation()
        }
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

    private fun reloadNavigation() {
        view?.apply {
            showPreButton(!currentDate.minusDays(7).isHolidays)
            showNextButton(!currentDate.plusDays(7).isHolidays)
            updateNavigationWeek("${currentDate.monday.toFormattedString("dd.MM")} - " +
                currentDate.sunday.toFormattedString("dd.MM"))
        }
    }

    fun onDateSet(year: Int, month: Int, day: Int) {
        reloadView(LocalDate.of(year, month, day))
        loadData()
    }

    fun onPickDate() {
        view?.showDatePickerDialog(currentDate)
    }

    fun onPreviousWeek() {
        reloadView(currentDate.minusDays(7))
        loadData()
    }

    fun onNextWeek() {
        reloadView(currentDate.plusDays(7))
        loadData()
    }
}
