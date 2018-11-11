package io.github.wulkanowy.ui.modules.note

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.ui.base.BaseView

interface NoteView : BaseView {

    val isViewEmpty: Boolean

    fun initView()

    fun updateData(data: List<NoteItem>)

    fun updateItem(item: AbstractFlexibleItem<*>)

    fun clearData()

    fun showEmpty(show: Boolean)

    fun showProgress(show: Boolean)

    fun showContent(show: Boolean)

    fun hideRefresh()

    fun showNoteDialog(note: Note)
}
