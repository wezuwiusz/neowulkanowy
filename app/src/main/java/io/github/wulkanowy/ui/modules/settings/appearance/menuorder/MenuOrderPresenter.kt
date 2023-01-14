package io.github.wulkanowy.ui.modules.settings.appearance.menuorder

import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import timber.log.Timber
import javax.inject.Inject

class MenuOrderPresenter @Inject constructor(
    studentRepository: StudentRepository,
    errorHandler: ErrorHandler,
    private val preferencesRepository: PreferencesRepository
) : BasePresenter<MenuOrderView>(errorHandler, studentRepository) {

    private var updatedMenuOrderItems = emptyList<MenuOrderItem>()

    override fun onAttachView(view: MenuOrderView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Menu order view was initialized")
        loadData()
    }

    private fun loadData() {
        val savedMenuItemList = (preferencesRepository.appMenuItemOrder)
            .sortedBy { it.order }
            .map { MenuOrderItem(it, it.order) }

        view?.updateData(savedMenuItemList)
    }

    fun onDragAndDropEnd(list: List<MenuOrderItem>) {
        val updatedList = list.mapIndexed { index, menuOrderItem ->
            menuOrderItem.copy(order = index)
        }

        updatedMenuOrderItems = updatedList
        view?.updateData(updatedList)
    }

    fun onBackSelected() {
        if (updatedMenuOrderItems.isNotEmpty()) {
            view?.showRestartConfirmationDialog()
        } else {
            view?.popView()
        }
    }

    fun onConfirmRestart() {
        updatedMenuOrderItems.forEach {
            it.appMenuItem.apply {
                order = it.order
            }
        }

        preferencesRepository.appMenuItemOrder = updatedMenuOrderItems.map { it.appMenuItem }
        view?.restartApp()
    }

    fun onCancelRestart() {
        view?.popView()
    }
}
