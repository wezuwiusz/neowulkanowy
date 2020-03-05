package io.github.wulkanowy.ui.modules.about.logviewer

import io.github.wulkanowy.data.repositories.logger.LoggerRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.SchedulersProvider
import timber.log.Timber
import javax.inject.Inject

class LogViewerPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val loggerRepository: LoggerRepository
) : BasePresenter<LogViewerView>(errorHandler, studentRepository, schedulers) {

    override fun onAttachView(view: LogViewerView) {
        super.onAttachView(view)
        view.initView()
        loadLogFile()
    }

    fun onShareLogsSelected(): Boolean {
        disposable.add(loggerRepository.getLogFiles()
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({
                Timber.i("Loading logs files result: ${it.joinToString { it.name }}")
                view?.shareLogs(it)
            }, {
                Timber.i("Loading logs files result: An exception occurred")
                errorHandler.dispatch(it)
            }))
        return true
    }

    fun onRefreshClick() {
        loadLogFile()
    }

    private fun loadLogFile() {
        disposable.add(loggerRepository.getLastLogLines()
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({
                Timber.i("Loading last log file result: load ${it.size} lines")
                view?.setLines(it)
            }, {
                Timber.i("Loading last log file result: An exception occurred")
                errorHandler.dispatch(it)
            }))
    }
}
