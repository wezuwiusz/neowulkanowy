package io.github.wulkanowy.ui.modules.splash

import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.ui.modules.Destination
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class SplashPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val json: Json
) : BasePresenter<SplashView>(errorHandler, studentRepository) {

    fun onAttachView(view: SplashView, externalUrl: String?, startDestinationJson: String?) {
        super.onAttachView(view)

        val startDestination: Destination? = startDestinationJson?.let { json.decodeFromString(it) }

        if (!externalUrl.isNullOrBlank()) {
            view.openExternalUrlAndFinish(externalUrl)
            return
        }

        presenterScope.launch {
            runCatching { studentRepository.isCurrentStudentSet() }
                .onFailure(errorHandler::dispatch)
                .onSuccess {
                    if (it) {
                        view.openMainView(startDestination)
                    } else {
                        view.openLoginView()
                    }
                }
        }
    }
}
