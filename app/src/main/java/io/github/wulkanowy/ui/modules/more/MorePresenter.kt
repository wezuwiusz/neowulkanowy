package io.github.wulkanowy.ui.modules.more

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import timber.log.Timber
import javax.inject.Inject

class MorePresenter @Inject constructor(errorHandler: ErrorHandler) : BasePresenter<MoreView>(errorHandler) {

    override fun onAttachView(view: MoreView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("More view was initialized")
        loadData()
    }

    fun onItemSelected(item: AbstractFlexibleItem<*>?) {
        if (item is MoreItem) {
            Timber.i("Select more item \"${item.title}\"")
            view?.run {
                when (item.title) {
                    messagesRes?.first -> openMessagesView()
                    homeworkRes?.first -> openHomeworkView()
                    noteRes?.first -> openNoteView()
                    luckyNumberRes?.first -> openLuckyNumberView()
                    settingsRes?.first -> openSettingsView()
                    aboutRes?.first -> openAboutView()
                }
            }
        }
    }

    fun onViewReselected() {
        Timber.i("More view is reselected")
        view?.popView()
    }

    private fun loadData() {
        Timber.i("Load items for more view")
        view?.run {
            updateData(listOfNotNull(
                messagesRes?.let { MoreItem(it.first, it.second) },
                homeworkRes?.let { MoreItem(it.first, it.second) },
                noteRes?.let { MoreItem(it.first, it.second) },
                luckyNumberRes?.let { MoreItem(it.first, it.second) },
                settingsRes?.let { MoreItem(it.first, it.second) },
                aboutRes?.let { MoreItem(it.first, it.second) })
            )
        }
    }
}
