package io.github.wulkanowy.ui.modules.login

import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.repositories.WulkanowyRepository
import io.github.wulkanowy.data.repositories.isEndDateReached
import io.github.wulkanowy.services.sync.SyncManager
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class LoginPresenter @Inject constructor(
    private val wulkanowyRepository: WulkanowyRepository,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val syncManager: SyncManager
) : BasePresenter<LoginView>(errorHandler, studentRepository) {

    override fun onAttachView(view: LoginView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Login view was initialized")
    }

    fun updateSdkMappings() {
        presenterScope.launch {
            runCatching { wulkanowyRepository.fetchMapping() }
                .onFailure { Timber.e(it) }
        }
    }

    fun checkIfEnd() {
        if (isEndDateReached) {
            syncManager.stopSyncWorker()
            view?.navigateToEnd()
        }
    }
}
