package io.github.wulkanowy.ui.modules.message.tab

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.enums.MessageFolder
import io.github.wulkanowy.databinding.FragmentMessageTabBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.message.MessageFragment
import io.github.wulkanowy.ui.modules.message.preview.MessagePreviewFragment
import io.github.wulkanowy.ui.widgets.DividerItemDecoration
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@AndroidEntryPoint
class MessageTabFragment : BaseFragment<FragmentMessageTabBinding>(R.layout.fragment_message_tab),
    MessageTabView {

    @Inject
    lateinit var presenter: MessageTabPresenter

    @Inject
    lateinit var tabAdapter: MessageTabAdapter

    companion object {
        const val MESSAGE_TAB_FOLDER_ID = "message_tab_folder_id"

        fun newInstance(folder: MessageFolder): MessageTabFragment {
            return MessageTabFragment().apply {
                arguments = Bundle().apply {
                    putString(MESSAGE_TAB_FOLDER_ID, folder.name)
                }
            }
        }
    }

    override val isViewEmpty
        get() = tabAdapter.itemCount == 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @FlowPreview
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMessageTabBinding.bind(view)
        messageContainer = binding.messageTabRecycler
        presenter.onAttachView(this, MessageFolder.valueOf(
            (savedInstanceState ?: arguments)?.getString(MESSAGE_TAB_FOLDER_ID).orEmpty()
        ))
    }

    override fun initView() {
        with(tabAdapter) {
            onClickListener = presenter::onMessageItemSelected
            onChangesDetectedListener = ::resetListPosition
        }

        with(binding.messageTabRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = tabAdapter
            addItemDecoration(DividerItemDecoration(context))
        }
        with(binding) {
            messageTabSwipe.setOnRefreshListener { presenter.onSwipeRefresh() }
            messageTabErrorRetry.setOnClickListener { presenter.onRetry() }
            messageTabErrorDetails.setOnClickListener { presenter.onDetailsClick() }
        }
    }

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

    override fun updateData(data: List<Message>) {
        tabAdapter.setDataItems(data)
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

    override fun openMessage(message: Message) {
        (activity as? MainActivity)?.pushView(MessagePreviewFragment.newInstance(message))
    }

    override fun notifyParentDataLoaded() {
        (parentFragment as? MessageFragment)?.onChildFragmentLoaded()
    }

    fun onParentLoadData(forceRefresh: Boolean) {
        presenter.onParentViewLoadData(forceRefresh)
    }

    fun onParentDeleteMessage() {
        presenter.onDeleteMessage()
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
