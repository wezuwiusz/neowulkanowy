package io.github.wulkanowy.ui.modules.debug

import io.github.wulkanowy.R
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import timber.log.Timber
import javax.inject.Inject

class DebugPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
) : BasePresenter<DebugView>(errorHandler, studentRepository) {

    val items = listOf(
        DebugItem(R.string.logviewer_title),
        DebugItem(R.string.notification_debug_title),
    )

    override fun onAttachView(view: DebugView) {
        super.onAttachView(view)
        Timber.i("Debug view was initialized")

        with(view) {
            initView()
            setItems(items)
        }
    }

    fun onItemSelect(item: DebugItem) {
        when (item.title) {
            R.string.logviewer_title -> view?.openLogViewer()
            R.string.notification_debug_title -> view?.openNotificationsDebug()
            else -> Timber.d("Unknown debug item: $item")
        }
    }
}
