package io.github.wulkanowy.ui.modules.message.preview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.session.BaseSessionFragment
import io.github.wulkanowy.ui.modules.main.MainView
import kotlinx.android.synthetic.main.fragment_message_preview.*
import javax.inject.Inject

class MessagePreviewFragment : BaseSessionFragment(), MessagePreviewView, MainView.TitledView {

    @Inject
    lateinit var presenter: MessagePreviewPresenter

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_message_preview, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        messageContainer = message
        presenter.onAttachView(this, (savedInstanceState ?: arguments)?.getInt(MESSAGE_ID_KEY) ?: 0)
    }

    override fun setSubject(subject: String) {
        messageSubject.text = subject
    }

    override fun setRecipient(recipient: String?) {
        messageAuthor.text = getString(R.string.message_to, recipient)
    }

    override fun setSender(sender: String?) {
        messageAuthor.text = getString(R.string.message_from, sender)
    }

    override fun setDate(date: String?) {
        messageDate.text = getString(R.string.message_date, date)
    }

    override fun setContent(content: String?) {
        messageContent.text = content
    }

    override fun showProgress(show: Boolean) {
        messageProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showMessageError() {
        messageError.visibility = View.VISIBLE
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
