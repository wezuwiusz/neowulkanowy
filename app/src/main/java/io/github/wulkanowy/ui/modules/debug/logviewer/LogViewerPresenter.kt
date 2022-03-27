package io.github.wulkanowy.ui.modules.debug.logviewer

import io.github.wulkanowy.data.Resource
import io.github.wulkanowy.data.repositories.LoggerRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.resourceFlow
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class LogViewerPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val loggerRepository: LoggerRepository
) : BasePresenter<LogViewerView>(errorHandler, studentRepository) {

    override fun onAttachView(view: LogViewerView) {
        super.onAttachView(view)
        view.initView()
        loadLogFile()
    }

    fun onShareLogsSelected(): Boolean {
        resourceFlow { loggerRepository.getLogFiles() }
            .onEach {
                when (it) {
                    is Resource.Loading -> Timber.d("Loading logs files started")
                    is Resource.Success -> {
                        Timber.i("Loading logs files result: ${it.data.joinToString { file -> file.name }}")
                        view?.shareLogs(it.data)
                    }
                    is Resource.Error -> {
                        Timber.i("Loading logs files result: An exception occurred")
                        errorHandler.dispatch(it.error)
                    }
                }
            }
            .launch("share")
        return true
    }

    fun onRefreshClick() {
        loadLogFile()
    }

    private fun loadLogFile() {
        resourceFlow { loggerRepository.getLastLogLines() }
            .onEach {
                when (it) {
                    is Resource.Loading -> Timber.d("Loading last log file started")
                    is Resource.Success -> {
                        Timber.i("Loading last log file result: load ${it.data.size} lines")
                        view?.setLines(it.data)
                    }
                    is Resource.Error -> {
                        Timber.i("Loading last log file result: An exception occurred")
                        errorHandler.dispatch(it.error)
                    }
                }
            }
            .launch("file")
    }
}
