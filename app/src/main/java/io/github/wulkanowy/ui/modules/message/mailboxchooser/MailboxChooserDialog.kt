package io.github.wulkanowy.ui.modules.message.mailboxchooser

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.data.db.entities.Mailbox
import io.github.wulkanowy.databinding.DialogMailboxChooserBinding
import io.github.wulkanowy.ui.base.BaseDialogFragment
import io.github.wulkanowy.utils.parcelableArray
import javax.inject.Inject

@AndroidEntryPoint
class MailboxChooserDialog : BaseDialogFragment<DialogMailboxChooserBinding>(), MailboxChooserView {

    @Inject
    lateinit var presenter: MailboxChooserPresenter

    @Inject
    lateinit var mailboxAdapter: MailboxChooserAdapter

    companion object {
        const val LISTENER_KEY = "mailbox_selected"
        const val MAILBOX_KEY = "selected_mailbox"
        const val REQUIRED_KEY = "is_mailbox_required"

        fun newInstance(mailboxes: List<Mailbox>, isMailboxRequired: Boolean, folder: String) =
            MailboxChooserDialog().apply {
                arguments = bundleOf(
                    MAILBOX_KEY to mailboxes.toTypedArray(),
                    REQUIRED_KEY to isMailboxRequired,
                    LISTENER_KEY to folder,
                )
            }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), theme)
            .setView(
                DialogMailboxChooserBinding.inflate(layoutInflater).apply { binding = this }.root
            )
            .create()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onAttachView(
            view = this,
            requireMailbox = requireArguments().getBoolean(REQUIRED_KEY, false),
            mailboxes = requireArguments().parcelableArray<Mailbox>(MAILBOX_KEY).orEmpty().toList(),
        )
    }

    override fun initView() {
        binding.accountQuickDialogRecycler.adapter = mailboxAdapter
    }

    override fun submitData(items: List<MailboxChooserItem>) {
        mailboxAdapter.submitList(items)
    }

    override fun onMailboxSelected(item: Mailbox?) {
        setFragmentResult(
            requestKey = requireArguments().getString(LISTENER_KEY).orEmpty(),
            result = bundleOf(MAILBOX_KEY to item),
        )
        dismiss()
    }
}
