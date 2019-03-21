package io.github.wulkanowy.ui.modules.message.preview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.ui.base.session.BaseSessionFragment
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.message.send.SendMessageActivity
import kotlinx.android.synthetic.main.fragment_message_preview.*
import javax.inject.Inject

@SuppressLint("SetTextI18n")
class MessagePreviewFragment : BaseSessionFragment(), MessagePreviewView, MainView.TitledView {

    @Inject
    lateinit var presenter: MessagePreviewPresenter

    private var menuReplyButton: MenuItem? = null
    private var menuForwardButton: MenuItem? = null

    override val titleStringId: Int
        get() = R.string.message_title

    override val noSubjectString: String
        get() = getString(R.string.message_no_subject)

    companion object {
        const val MESSAGE_ID_KEY = "message_id"

        fun newInstance(messageId: Int?): MessagePreviewFragment {
            return MessagePreviewFragment().apply {
                arguments = Bundle().apply { putInt(MESSAGE_ID_KEY, messageId ?: 0) }
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
        presenter.onAttachView(this, (savedInstanceState ?: arguments)?.getInt(MESSAGE_ID_KEY) ?: 0)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.action_menu_message_preview, menu)
        menuReplyButton = menu?.findItem(R.id.messagePreviewMenuReply)
        menuForwardButton = menu?.findItem(R.id.messagePreviewMenuForward)
        presenter.onCreateOptionsMenu()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.messagePreviewMenuReply -> presenter.onReply()
            R.id.messagePreviewMenuForward -> presenter.onForward()
            else -> false
        }
    }

    override fun setSubject(subject: String) {
        messagePreviewSubject.text = subject
    }

    override fun setRecipient(recipient: String) {
        messagePreviewAuthor.text = "${getString(R.string.message_to)} $recipient"
    }

    override fun setSender(sender: String) {
        messagePreviewAuthor.text = "${getString(R.string.message_from)} $sender"
    }

    override fun setDate(date: String) {
        messagePreviewDate.text = getString(R.string.message_date, date)
    }

    override fun setContent(content: String) {
        messagePreviewContent.text = content
    }

    override fun showProgress(show: Boolean) {
        messagePreviewProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun showOptions(show: Boolean) {
        menuReplyButton?.isVisible = show
        menuForwardButton?.isVisible = show
    }

    override fun showMessageError() {
        messagePreviewError.visibility = VISIBLE
    }

    override fun openMessageReply(message: Message?) {
        context?.let { it.startActivity(SendMessageActivity.getStartIntent(it, message, true)) }
    }

    override fun openMessageForward(message: Message?) {
        context?.let { it.startActivity(SendMessageActivity.getStartIntent(it, message)) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(MESSAGE_ID_KEY, presenter.messageId)
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
