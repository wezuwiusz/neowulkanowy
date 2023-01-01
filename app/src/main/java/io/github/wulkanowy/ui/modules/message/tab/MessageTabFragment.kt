package io.github.wulkanowy.ui.modules.message.tab

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.*
import android.widget.CompoundButton
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.core.view.updatePadding
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Mailbox
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.enums.MessageFolder
import io.github.wulkanowy.databinding.FragmentMessageTabBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.message.MessageFragment
import io.github.wulkanowy.ui.modules.message.mailboxchooser.MailboxChooserDialog
import io.github.wulkanowy.ui.modules.message.preview.MessagePreviewFragment
import io.github.wulkanowy.ui.widgets.DividerItemDecoration
import io.github.wulkanowy.utils.dpToPx
import io.github.wulkanowy.utils.getThemeAttrColor
import io.github.wulkanowy.utils.hideSoftInput
import io.github.wulkanowy.utils.nullableSerializable
import javax.inject.Inject

@AndroidEntryPoint
class MessageTabFragment : BaseFragment<FragmentMessageTabBinding>(R.layout.fragment_message_tab),
    MessageTabView {

    @Inject
    lateinit var presenter: MessageTabPresenter

    @Inject
    lateinit var messageTabAdapter: MessageTabAdapter

    companion object {

        const val MESSAGE_TAB_FOLDER_ID = "message_tab_folder_id"

        fun newInstance(folder: MessageFolder) = MessageTabFragment().apply {
            arguments = bundleOf(MESSAGE_TAB_FOLDER_ID to folder.name)
        }
    }

    override val isViewEmpty
        get() = messageTabAdapter.itemCount == 0

    private var actionMode: ActionMode? = null

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            val inflater = mode.menuInflater
            inflater.inflate(R.menu.context_menu_message_tab, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            if (presenter.folder == MessageFolder.TRASHED) {
                val menuItem = menu.findItem(R.id.messageTabContextMenuDelete)
                menuItem.setTitle(R.string.message_delete_forever)
            }
            return presenter.onPrepareActionMode()
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            presenter.onDestroyActionMode()
            actionMode = null
        }

        override fun onActionItemClicked(mode: ActionMode, menu: MenuItem): Boolean {
            when (menu.itemId) {
                R.id.messageTabContextMenuDelete -> presenter.onActionModeSelectDelete()
                R.id.messageTabContextMenuSelectAll -> presenter.onActionModeSelectCheckAll()
            }
            return true
        }
    }

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMessageTabBinding.bind(view)
        messageContainer = binding.messageTabRecycler

        val folder = MessageFolder.valueOf(
            (savedInstanceState ?: requireArguments()).getString(MESSAGE_TAB_FOLDER_ID).orEmpty()
        )
        presenter.onAttachView(this, folder)
    }

    override fun initView() {
        with(messageTabAdapter) {
            onItemClickListener = presenter::onMessageItemSelected
            onLongItemClickListener = presenter::onMessageItemLongSelected
            onHeaderClickListener = ::onChipChecked
            onMailboxClickListener = presenter::onMailboxFilterSelected
            onChangesDetectedListener = ::resetListPosition
        }

        with(binding.messageTabRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = messageTabAdapter
            addItemDecoration(DividerItemDecoration(context, false))
            itemAnimator = null
        }

        with(binding) {
            messageTabSwipe.setOnRefreshListener(presenter::onSwipeRefresh)
            messageTabSwipe.setColorSchemeColors(requireContext().getThemeAttrColor(R.attr.colorPrimary))
            messageTabSwipe.setProgressBackgroundColorSchemeColor(
                requireContext().getThemeAttrColor(R.attr.colorSwipeRefresh)
            )
            messageTabErrorRetry.setOnClickListener { presenter.onRetry() }
            messageTabErrorDetails.setOnClickListener { presenter.onDetailsClick() }
        }

        setFragmentResultListener(requireArguments().getString(MESSAGE_TAB_FOLDER_ID)!!) { _, bundle ->
            presenter.onMailboxSelected(
                mailbox = bundle.nullableSerializable(MailboxChooserDialog.MAILBOX_KEY),
            )
        }
    }

    @Suppress("DEPRECATION")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.action_menu_message_tab, menu)

        val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.queryHint = getString(R.string.all_search_hint)
        searchView.maxWidth = Int.MAX_VALUE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String) = false
            override fun onQueryTextChange(query: String): Boolean {
                presenter.onSearchQueryTextChange(query)
                return true
            }
        })
    }

    override fun updateData(data: List<MessageTabDataItem>) {
        messageTabAdapter.submitData(data)
    }

    override fun updateActionModeTitle(selectedMessagesSize: Int) {
        actionMode?.title = resources.getQuantityString(
            R.plurals.message_selected_messages_count,
            selectedMessagesSize,
            selectedMessagesSize
        )
    }

    override fun updateSelectAllMenu(isAllSelected: Boolean) {
        val menuItem = actionMode?.menu?.findItem(R.id.messageTabContextMenuSelectAll) ?: return

        if (isAllSelected) {
            menuItem.setTitle(R.string.message_unselect_all)
            menuItem.setIcon(R.drawable.ic_message_unselect_all)
        } else {
            menuItem.setTitle(R.string.message_select_all)
            menuItem.setIcon(R.drawable.ic_message_select_all)
        }
    }

    override fun showProgress(show: Boolean) {
        binding.messageTabProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun enableSwipe(enable: Boolean) {
        binding.messageTabSwipe.isEnabled = enable
    }

    override fun resetListPosition() {
        binding.messageTabRecycler.scrollToPosition(0)
    }

    override fun showContent(show: Boolean) {
        binding.messageTabRecycler.visibility = if (show) VISIBLE else INVISIBLE
    }

    override fun showEmpty(show: Boolean) {
        binding.messageTabEmpty.visibility = if (show) VISIBLE else INVISIBLE
    }

    override fun showErrorView(show: Boolean) {
        binding.messageTabError.visibility = if (show) VISIBLE else GONE
    }

    override fun setErrorDetails(message: String) {
        binding.messageTabErrorMessage.text = message
    }

    override fun showRefresh(show: Boolean) {
        binding.messageTabSwipe.isRefreshing = show
    }

    override fun showMessagesDeleted() {
        showMessage(getString(R.string.message_messages_deleted))
    }

    override fun notifyParentShowNewMessage(show: Boolean) {
        (parentFragment as? MessageFragment)?.onChildFragmentShowNewMessage(show)
    }

    override fun openMessage(message: Message) {
        (activity as? MainActivity)?.pushView(MessagePreviewFragment.newInstance(message))
    }

    override fun notifyParentDataLoaded() {
        (parentFragment as? MessageFragment)?.onChildFragmentLoaded()
    }

    override fun notifyParentShowActionMode(show: Boolean) {
        (parentFragment as? MessageFragment)?.onChildFragmentShowActionMode(show)
    }

    fun onParentLoadData(forceRefresh: Boolean) {
        presenter.onParentViewLoadData(forceRefresh)
    }

    fun onParentFinishActionMode() {
        presenter.onParentFinishActionMode()
    }

    private fun onChipChecked(chip: CompoundButton, isChecked: Boolean) {
        when (chip.id) {
            R.id.chip_unread -> presenter.onUnreadFilterSelected(isChecked)
            R.id.chip_attachments -> presenter.onAttachmentsFilterSelected(isChecked)
        }
    }

    override fun showActionMode(show: Boolean) {
        if (show) {
            actionMode = (activity as MainActivity?)?.startSupportActionMode(actionModeCallback)
        } else {
            actionMode?.finish()
        }
    }

    override fun showRecyclerBottomPadding(show: Boolean) {
        binding.messageTabRecycler.updatePadding(
            bottom = if (show) requireContext().dpToPx(64f).toInt() else 0
        )
    }

    override fun showMailboxChooser(mailboxes: List<Mailbox>) {
        (activity as? MainActivity)?.showDialogFragment(
            MailboxChooserDialog.newInstance(
                mailboxes = mailboxes,
                isMailboxRequired = false,
                folder = requireArguments().getString(MESSAGE_TAB_FOLDER_ID)!!,
            )
        )
    }

    override fun hideKeyboard() {
        activity?.hideSoftInput()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(MESSAGE_TAB_FOLDER_ID, presenter.folder.name)
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
