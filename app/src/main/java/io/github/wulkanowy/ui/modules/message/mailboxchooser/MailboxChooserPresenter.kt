package io.github.wulkanowy.ui.modules.message.mailboxchooser

import io.github.wulkanowy.data.db.entities.Mailbox
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import timber.log.Timber
import javax.inject.Inject

class MailboxChooserPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository
) : BasePresenter<MailboxChooserView>(errorHandler, studentRepository) {

    fun onAttachView(view: MailboxChooserView, mailboxes: List<Mailbox>, requireMailbox: Boolean) {
        super.onAttachView(view)

        view.initView()
        Timber.i("Mailbox chooser view was initialized")
        view.submitData(getMailboxItems(mailboxes, requireMailbox))
    }

    private fun getMailboxItems(
        mailboxes: List<Mailbox>,
        requireMailbox: Boolean,
    ): List<MailboxChooserItem> = buildList {
        if (!requireMailbox) {
            add(MailboxChooserItem(isAll = true, onClickListener = ::onMailboxSelect))
        }
        addAll(mailboxes.map {
            MailboxChooserItem(mailbox = it, isAll = false, onClickListener = ::onMailboxSelect)
        })
    }

    fun onMailboxSelect(item: Mailbox?) {
        view?.onMailboxSelected(item)
    }
}
