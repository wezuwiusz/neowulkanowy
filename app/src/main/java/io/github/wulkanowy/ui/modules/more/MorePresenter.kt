package io.github.wulkanowy.ui.modules.more

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.ui.base.BasePresenter
import javax.inject.Inject

class MorePresenter @Inject constructor(errorHandler: ErrorHandler) : BasePresenter<MoreView>(errorHandler) {

    override fun onAttachView(view: MoreView) {
        super.onAttachView(view)
        view.initView()
        loadData()
    }

    fun onItemSelected(item: AbstractFlexibleItem<*>?) {
        if (item is MoreItem) {
            view?.run {
                when (item.title) {
                    messagesRes?.first -> openMessagesView()
                    homeworkRes?.first -> openHomeworkView()
                    noteRes?.first -> openNoteView()
                    settingsRes?.first -> openSettingsView()
                    aboutRes?.first -> openAboutView()
                }
            }
        }
    }

    fun onViewReselected() {
        view?.popView()
    }

    private fun loadData() {
        view?.run {
            updateData(listOfNotNull(
                messagesRes?.let { MoreItem(it.first, it.second) },
                homeworkRes?.let { MoreItem(it.first, it.second) },
                noteRes?.let { MoreItem(it.first, it.second) },
                settingsRes?.let { MoreItem(it.first, it.second) },
                aboutRes?.let { MoreItem(it.first, it.second) })
            )
        }
    }
}
