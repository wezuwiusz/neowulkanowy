package io.github.wulkanowy.ui.modules.about.license

import com.mikepenz.aboutlibraries.entity.Library
import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.DispatchersProvider
import io.github.wulkanowy.utils.afterLoading
import io.github.wulkanowy.utils.flowWithResource
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class LicensePresenter @Inject constructor(
    private val dispatchers: DispatchersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository
) : BasePresenter<LicenseView>(errorHandler, studentRepository) {

    override fun onAttachView(view: LicenseView) {
        super.onAttachView(view)
        view.initView()
        loadData()
    }

    fun onItemSelected(library: Library) {
        view?.run { library.licenses?.firstOrNull()?.licenseDescription?.let { openLicense(it) } }
    }

    private fun loadData() {
        flowWithResource {
            withContext(dispatchers.io) {
                view?.appLibraries.orEmpty()
            }
        }.onEach {
            when (it.status) {
                Status.LOADING -> Timber.d("License data load started")
                Status.SUCCESS -> view?.updateData(it.data!!)
                Status.ERROR -> errorHandler.dispatch(it.error!!)
            }
        }.afterLoading {
            view?.showProgress(false)
        }.launch()
    }
}
