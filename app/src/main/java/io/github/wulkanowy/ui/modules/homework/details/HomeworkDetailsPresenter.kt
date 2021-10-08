package io.github.wulkanowy.ui.modules.homework.details

import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.data.repositories.HomeworkRepository
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.flowWithResource
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class HomeworkDetailsPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val homeworkRepository: HomeworkRepository,
    private val analytics: AnalyticsHelper,
    private val preferencesRepository: PreferencesRepository
) : BasePresenter<HomeworkDetailsView>(errorHandler, studentRepository) {

    var isHomeworkFullscreen
        get() = preferencesRepository.isHomeworkFullscreen
        set(value) {
            preferencesRepository.isHomeworkFullscreen = value
        }

    override fun onAttachView(view: HomeworkDetailsView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Homework details view was initialized")
    }

    fun deleteHomework(homework: Homework) {
        flowWithResource { homeworkRepository.deleteHomework(homework) }.onEach {
            when (it.status) {
                Status.LOADING -> Timber.i("Homework delete start")
                Status.SUCCESS -> {
                    Timber.i("Homework delete: Success")
                    view?.run {
                        showMessage(homeworkDeleteSuccess)
                        closeDialog()
                    }
                }
                Status.ERROR -> {
                    Timber.i("Homework delete result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                }
            }
        }.launch("delete")
    }

    fun toggleDone(homework: Homework) {
        flowWithResource { homeworkRepository.toggleDone(homework) }.onEach {
            when (it.status) {
                Status.LOADING -> Timber.i("Homework details update start")
                Status.SUCCESS -> {
                    Timber.i("Homework details update: Success")
                    view?.updateMarkAsDoneLabel(homework.isDone)
                    analytics.logEvent("homework_mark_as_done")
                }
                Status.ERROR -> {
                    Timber.i("Homework details update result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                }
            }
        }.launch("toggle")
    }
}
