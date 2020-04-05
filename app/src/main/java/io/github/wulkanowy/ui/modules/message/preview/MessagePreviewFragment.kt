package io.github.wulkanowy.ui.modules.message.preview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.MessageWithAttachment
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.message.MessageFragment
import io.github.wulkanowy.ui.modules.message.send.SendMessageActivity
import kotlinx.android.synthetic.main.fragment_message_preview.*
import javax.inject.Inject

class MessagePreviewFragment : BaseFragment(), MessagePreviewView, MainView.TitledView {

    @Inject
    lateinit var presenter: MessagePreviewPresenter

    @Inject
    lateinit var previewAdapter: MessagePreviewAdapter

    private var menuReplyButton: MenuItem? = null

    private var menuForwardButton: MenuItem? = null

    private var menuDeleteButton: MenuItem? = null

    override val titleStringId: Int
        get() = R.string.message_title

    override val deleteMessageSuccessString: String
        get() = getString(R.string.message_delete_success)

    companion object {
        const val MESSAGE_ID_KEY = "message_id"

        fun newInstance(message: Message): MessagePreviewFragment {
            return MessagePreviewFragment().apply {
                arguments = Bundle().apply { putSerializable(MESSAGE_ID_KEY, message) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_message_preview, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        messageContainer = messagePreviewContainer
        presenter.onAttachView(this, (savedInstanceState ?: arguments)?.getSerializable(MESSAGE_ID_KEY) as Message)
    }

    override fun initView() {
        messagePreviewErrorDetails.setOnClickListener { presenter.onDetailsClick() }

        with(messagePreviewRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = previewAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_menu_message_preview, menu)
        menuReplyButton = menu.findItem(R.id.messagePreviewMenuReply)
        menuForwardButton = menu.findItem(R.id.messagePreviewMenuForward)
        menuDeleteButton = menu.findItem(R.id.messagePreviewMenuDelete)
        presenter.onCreateOptionsMenu()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.messagePreviewMenuReply -> presenter.onReply()
            R.id.messagePreviewMenuForward -> presenter.onForward()
            R.id.messagePreviewMenuDelete -> presenter.onMessageDelete()
            else -> false
        }
    }

    override fun setMessageWithAttachment(item: MessageWithAttachment) {
        with(previewAdapter) {
            messageWithAttachment = item
            notifyDataSetChanged()
        }
    }

    override fun showProgress(show: Boolean) {
        messagePreviewProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun showContent(show: Boolean) {
        messagePreviewRecycler.visibility = if (show) VISIBLE else GONE
    }

    override fun showOptions(show: Boolean) {
        menuReplyButton?.isVisible = show
        menuForwardButton?.isVisible = show
        menuDeleteButton?.isVisible = show
    }

    override fun setDeletedOptionsLabels() {
        menuDeleteButton?.setTitle(R.string.message_delete_forever)
    }

    override fun setNotDeletedOptionsLabels() {
        menuDeleteButton?.setTitle(R.string.message_move_to_bin)
    }

    override fun showErrorView(show: Boolean) {
        messagePreviewError.visibility = if (show) VISIBLE else GONE
    }

    override fun setErrorDetails(message: String) {
        messagePreviewErrorMessage.text = message
    }

    override fun setErrorRetryCallback(callback: () -> Unit) {
        messagePreviewErrorRetry.setOnClickListener { callback() }
    }

    override fun openMessageReply(message: Message?) {
        context?.let { it.startActivity(SendMessageActivity.getStartIntent(it, message, true)) }
    }

    override fun openMessageForward(message: Message?) {
        context?.let { it.startActivity(SendMessageActivity.getStartIntent(it, message)) }
    }

    override fun popView() {
        (activity as MainActivity).popView()
    }

    override fun notifyParentMessageDeleted(message: Message) {
        parentFragmentManager.fragments.forEach { if (it is MessageFragment) it.onDeleteMessage(message) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(MESSAGE_ID_KEY, presenter.message)
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
