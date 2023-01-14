package io.github.wulkanowy.ui.modules.more

import io.github.wulkanowy.R
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.ui.modules.Destination
import timber.log.Timber
import javax.inject.Inject

class MorePresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    preferencesRepository: PreferencesRepository
) : BasePresenter<MoreView>(errorHandler, studentRepository) {

    private val moreAppMenuItem = preferencesRepository.appMenuItemOrder
        .sortedBy { it.order }
        .drop(4)

    override fun onAttachView(view: MoreView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("More view was initialized")
        loadData()
    }

    fun onItemSelected(moreItem: MoreItem) {
        Timber.i("Select more item \"${moreItem.destination.destinationType}\"")

        view?.openView(moreItem.destination)
    }

    fun onViewReselected() {
        Timber.i("More view is reselected")
        view?.popView(2)
    }

    private fun loadData() {
        Timber.i("Load items for more view")
        val moreItems = moreAppMenuItem.map {
            MoreItem(
                icon = it.icon,
                title = it.title,
                destination = it.destinationType.defaultDestination
            )
        }
            .plus(
                MoreItem(
                    icon = R.drawable.ic_more_settings,
                    title = R.string.settings_title,
                    destination = Destination.Settings
                )
            )

        view?.updateData(moreItems)
    }
}
