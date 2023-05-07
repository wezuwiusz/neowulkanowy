package io.github.wulkanowy.ui.modules.homework.details

import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.data.logResourceStatus
import io.github.wulkanowy.data.onResourceError
import io.github.wulkanowy.data.onResourceSuccess
import io.github.wulkanowy.data.repositories.HomeworkRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.resourceFlow
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import timber.log.Timber
import javax.inject.Inject

class HomeworkDetailsPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val homeworkRepository: HomeworkRepository,
    private val analytics: AnalyticsHelper,
) : BasePresenter<HomeworkDetailsView>(errorHandler, studentRepository) {

    override fun onAttachView(view: HomeworkDetailsView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Homework details view was initialized")
    }

    fun deleteHomework(homework: Homework) {
        resourceFlow { homeworkRepository.deleteHomework(homework) }
            .logResourceStatus("homework delete")
            .onResourceSuccess {
                view?.run {
                    showMessage(homeworkDeleteSuccess)
                    closeDialog()
                }
            }
            .onResourceError(errorHandler::dispatch)
            .launch("delete")
    }

    fun toggleDone(homework: Homework) {
        resourceFlow { homeworkRepository.toggleDone(homework) }
            .logResourceStatus("homework details update")
            .onResourceSuccess {
                view?.updateMarkAsDoneLabel(homework.isDone)
                analytics.logEvent("homework_mark_as_done")
            }
            .onResourceError(errorHandler::dispatch)
            .launch("toggle")
    }
}
