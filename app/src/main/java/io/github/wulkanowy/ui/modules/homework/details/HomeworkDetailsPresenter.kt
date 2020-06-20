package io.github.wulkanowy.ui.modules.homework.details

import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.data.repositories.homework.HomeworkRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import kotlinx.coroutines.rx2.rxSingle
import timber.log.Timber
import javax.inject.Inject

class HomeworkDetailsPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val homeworkRepository: HomeworkRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<HomeworkDetailsView>(errorHandler, studentRepository, schedulers) {

    override fun onAttachView(view: HomeworkDetailsView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Homework details view was initialized")
    }

    fun toggleDone(homework: Homework) {
        Timber.i("Homework details update start")
        disposable.add(rxSingle { homeworkRepository.toggleDone(homework) }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({
                Timber.i("Homework details update: Success")
                view?.run {
                    updateMarkAsDoneLabel(homework.isDone)
                }
                analytics.logEvent("homework_mark_as_done")
            }) {
                Timber.i("Homework details update result: An exception occurred")
                errorHandler.dispatch(it)
            }
        )
    }
}
